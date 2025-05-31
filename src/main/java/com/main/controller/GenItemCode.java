package com.main.controller;

import com.alibaba.fastjson.JSON;
import com.main.entity.po.InspectRequestData;
import com.main.entity.po.StickerData;
import com.main.entity.vo.ResponseVO;
import com.main.utils.Econ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CRC32;

@RestController
@RequestMapping("/getItemCode")
@CrossOrigin
public class GenItemCode extends ABaseController{
    private static final Logger logger = LoggerFactory.getLogger(GenItemCode.class);
    //将 float 转换为其大端序的4字节整数表示
    private int floatToIntBytesBigEndian(float floatVal) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putFloat(floatVal);
        buffer.rewind();
        return buffer.getInt();
    }
    //将 byte[] 转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes).toUpperCase();
    }

    @RequestMapping("/")
    public ResponseVO getItemCode(@RequestBody InspectRequestData requestData) {
        try {
            logger.info(JSON.toJSONString(requestData));
            Econ.CEconItemPreviewDataBlock.Builder econBuilder = Econ.CEconItemPreviewDataBlock.newBuilder();
            //填充数据
            if (!requestData.itemId.isEmpty()) {
                econBuilder.setItemid(Long.parseLong(requestData.itemId));
            }
            //稀有度
            if(!requestData.rarity.isEmpty()){
                econBuilder.setRarity(Integer.parseInt(requestData.rarity));
            }
            //物品id
            if(!requestData.gunIndex.isEmpty()){
                econBuilder.setDefindex(Integer.parseInt(requestData.gunIndex));
            }
            //皮肤id
            if(!requestData.skinIndex.isEmpty()){
                econBuilder.setPaintindex(Integer.parseInt(requestData.skinIndex));
            }

            //模板编号
            if(!requestData.skinIndex.isEmpty()){
                econBuilder.setPaintseed(Integer.parseInt(requestData.pattern));
            }

            //磨损度
            if(!requestData.wear.isEmpty()){
                logger.info("wear:" + requestData.wear);
                econBuilder.setPaintwear(Integer.parseInt(requestData.wear));
            }

            //是否是StatTrak
            if(Boolean.TRUE.equals(requestData.statTrak)){
                econBuilder.setKilleatervalue(0);
                econBuilder.setKilleatervalue(Integer.parseInt(requestData.statTrakCount));
            }

            //是否有标签
            if(requestData.nameTag != null){
                econBuilder.setCustomname(requestData.nameTag);
            }
            //是否有贴纸
            if(requestData.stickers != null){
                for(StickerData stickerData : requestData.stickers){
                    if(!stickerData.name.isEmpty()){
                        Econ.CEconItemPreviewDataBlock.Sticker.Builder stickerBuilder = Econ.CEconItemPreviewDataBlock.Sticker.newBuilder();
                        stickerBuilder.setStickerId(Integer.parseInt(stickerData.name));
                        if(!stickerData.slot.isEmpty())stickerBuilder.setSlot(Integer.parseInt(stickerData.slot));
                        if(!stickerData.wear.isEmpty())stickerBuilder.setWear(floatToIntBytesBigEndian(Float.parseFloat(stickerData.wear)));
                        if(!stickerData.rotation.isEmpty())stickerBuilder.setRotation(floatToIntBytesBigEndian(Float.parseFloat(stickerData.rotation)));
                        if(!stickerData.x.isEmpty())stickerBuilder.setOffsetX(floatToIntBytesBigEndian(Float.parseFloat(stickerData.x)));
                        if(!stickerData.y.isEmpty())stickerBuilder.setOffsetY(floatToIntBytesBigEndian(Float.parseFloat(stickerData.y)));
                        econBuilder.addStickers(stickerBuilder.build());
                    }
                }
            }
            //是否有挂件
            if(!Objects.equals(requestData.charm.name,"")){
                Econ.CEconItemPreviewDataBlock.Sticker.Builder stickerBuilder = Econ.CEconItemPreviewDataBlock.Sticker.newBuilder();
                stickerBuilder.setStickerId(Integer.parseInt(requestData.charm.name));
                if(!requestData.charm.pattern.isEmpty())stickerBuilder.setPattern(Integer.parseInt(requestData.charm.pattern));
                if(!requestData.charm.slot.isEmpty())stickerBuilder.setSlot(Integer.parseInt(requestData.charm.slot));
                if(!requestData.charm.x.isEmpty())stickerBuilder.setOffsetX(floatToIntBytesBigEndian(Float.parseFloat(requestData.charm.x)));
                if(!requestData.charm.y.isEmpty())stickerBuilder.setOffsetY(floatToIntBytesBigEndian(Float.parseFloat(requestData.charm.y)));
                econBuilder.addKeychains(stickerBuilder.build());
            }

            Econ.CEconItemPreviewDataBlock econInstance = econBuilder.build();
            byte[] serializedProto = econInstance.toByteArray();
            ByteBuffer bufferForCrc = ByteBuffer.allocate(1 + serializedProto.length);
            bufferForCrc.put((byte) 0);
            bufferForCrc.put(serializedProto);
            byte[] crcInputBytes = bufferForCrc.array();
            CRC32 crc32 = new CRC32();
            crc32.update(crcInputBytes);
            long crcValue = crc32.getValue();

            int protoByteSize = serializedProto.length;
            long crcLower16 = crcValue & 0xFFFFL;
            long factor = (long)protoByteSize * crcValue;
            int xoredCrcUnsigned = (int) ((crcLower16 ^ factor) & 0xFFFFFFFFL);

            ByteBuffer checksumBuffer = ByteBuffer.allocate(4);
            checksumBuffer.order(ByteOrder.BIG_ENDIAN);
            checksumBuffer.putInt(xoredCrcUnsigned);
            byte[] checksumBytes = checksumBuffer.array();

            ByteBuffer finalBuffer = ByteBuffer.allocate(crcInputBytes.length + checksumBytes.length);
            finalBuffer.put(crcInputBytes);
            finalBuffer.put(checksumBytes);

            String hexString = bytesToHex(finalBuffer.array());
            logger.info(hexString);
            Map<String,Object> map = new HashMap<>();
            map.put("inspectCode", "csgo_econ_action_preview " + hexString);

            return getSuccessResponseVO(map);
        }catch (Exception e){
            return getServerErrorResponseVO(e.getMessage());
        }
    }
}
