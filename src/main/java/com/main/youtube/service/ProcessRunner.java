package com.main.youtube.service;

import com.main.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
public class ProcessRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProcessRunner.class);

    /**
     * 同步执行并收集全部 stdout，用于 yt-dlp -J 这类 JSON 输出场景。
     */
    public String run(List<String> command, Duration timeout) {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        int exitCode = execute(command, timeout, null, stdout, stderr);
        if (exitCode != 0) {
            throw new BusinessException("yt-dlp 执行失败 (exit=" + exitCode + "): " + abbreviate(stderr.toString()));
        }
        return stdout.toString();
    }

    /**
     * 流式执行，逐行回调 stdout/stderr（已合并），返回退出码。
     * processRef 可用于外部取消（destroy）。
     */
    public int runStreaming(List<String> command, Duration timeout,
                            Consumer<String> onLine, AtomicReference<Process> processRef) {
        StringBuilder stderr = new StringBuilder();
        return execute(command, timeout, processRef, null, stderr, onLine);
    }

    private int execute(List<String> command, Duration timeout, AtomicReference<Process> processRef,
                        StringBuilder stdout, StringBuilder stderr) {
        return execute(command, timeout, processRef, stdout, stderr, null);
    }

    private int execute(List<String> command, Duration timeout, AtomicReference<Process> processRef,
                        StringBuilder stdout, StringBuilder stderr, Consumer<String> onLine) {
        logger.info("执行命令: {}", String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.environment().put("PYTHONIOENCODING", "utf-8");
        // stderr 合入 stdout 统一读取；JSON 模式下 stdout 会被污染，所以 JSON 模式不合并
        if (onLine != null) {
            pb.redirectErrorStream(true);
        }

        Process process;
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new BusinessException("无法启动进程，请检查 yt-dlp 是否已安装: " + e.getMessage(), e);
        }

        if (processRef != null) {
            processRef.set(process);
        }

        try {
            if (onLine != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        onLine.accept(line);
                    }
                }
            } else {
                // stdout / stderr 并发消费，避免缓冲区打满死锁
                Thread outReader = Thread.startVirtualThread(() -> drain(process.getInputStream(), stdout));
                Thread errReader = Thread.startVirtualThread(() -> drain(process.getErrorStream(), stderr));

                boolean exited = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
                if (!exited) {
                    killTree(process);
                    throw new BusinessException("yt-dlp 执行超时 (" + timeout.toSeconds() + "s)");
                }
                // 进程已退出，收尾剩余输出
                outReader.join(5000);
                errReader.join(5000);
                return process.exitValue();
            }

            boolean exited = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!exited) {
                killTree(process);
                throw new BusinessException("yt-dlp 执行超时 (" + timeout.toSeconds() + "s)");
            }
            return process.exitValue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            killTree(process);
            throw new BusinessException("进程被中断", e);
        } catch (IOException e) {
            killTree(process);
            throw new BusinessException("读取进程输出失败: " + e.getMessage(), e);
        }
    }

    private void drain(java.io.InputStream in, StringBuilder target) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                synchronized (target) {
                    target.append(line).append('\n');
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void killTree(Process process) {
        process.descendants().forEach(ProcessHandle::destroyForcibly);
        process.destroyForcibly();
    }

    private String abbreviate(String text) {
        String normalized = text == null ? "" : text.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 400 ? normalized : normalized.substring(normalized.length() - 400);
    }
}
