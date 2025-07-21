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
import java.util.*;
import java.util.zip.CRC32;

@RestController
@RequestMapping("/getItemCode")
public class GenItemCode extends ABaseController{
    private static final Logger logger = LoggerFactory.getLogger(GenItemCode.class);

    @RequestMapping("/")
    public ResponseVO getItemCode(@RequestBody InspectRequestData requestData) {
        try {
            logger.info(JSON.toJSONString(requestData));
            StringBuilder gencode = new StringBuilder("!gen ");
            Econ.CEconItemPreviewDataBlock.Builder econBuilder = Econ.CEconItemPreviewDataBlock.newBuilder();
            //辨别码
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
                gencode.append(requestData.gunIndex).append(" ");
            }
            //皮肤id
            if(!requestData.skinIndex.isEmpty()){
                econBuilder.setPaintindex(Integer.parseInt(requestData.skinIndex));
                gencode.append(requestData.skinIndex).append(" ");
            }

            //模板编号
            if(!requestData.pattern.isEmpty()){
                econBuilder.setPaintseed(Integer.parseInt(requestData.pattern));
                gencode.append(requestData.pattern).append(" ");
            }else {
                gencode.append(0).append(" ");
            }

            //磨损度
            if(!requestData.wear.isEmpty()){
                logger.info(requestData.wear);
                econBuilder.setPaintwear(Float.floatToIntBits(Float.parseFloat(requestData.wear)));
                gencode.append(requestData.wear).append(" ");
            }else {
                gencode.append(0).append(" ");
            }

            //音乐盒
            if(!requestData.musicIndex.isEmpty()){
                econBuilder.setMusicindex(Integer.parseInt(requestData.musicIndex));
            }

            if(!requestData.entIndex.isEmpty()){
                econBuilder.setEntindex(Integer.parseInt(requestData.entIndex));
            }

            if(!requestData.petIndex.isEmpty()){
                econBuilder.setPetindex(Integer.parseInt(requestData.petIndex));
            }

            //是否是StatTrak
            if(!requestData.statTrakCount.isEmpty()){
                econBuilder.setKilleatervalue(0);
                econBuilder.setKilleatervalue(Integer.parseInt(requestData.statTrakCount));
            }

            //是否有标签
            if(requestData.nameTag != null){
                econBuilder.setCustomname(requestData.nameTag);
            }
            //是否有贴纸
            for(StickerData stickerData : requestData.stickers){
                if(!stickerData.name.isEmpty()) {
                    Econ.CEconItemPreviewDataBlock.Sticker.Builder stickerBuilder = Econ.CEconItemPreviewDataBlock.Sticker.newBuilder();
                    stickerBuilder.setStickerId(Integer.parseInt(stickerData.name));
                    if (!stickerData.slot.isEmpty()) stickerBuilder.setSlot(Integer.parseInt(stickerData.slot));
                    if (!stickerData.wear.isEmpty())
                        stickerBuilder.setWear(Float.parseFloat(stickerData.wear));
                    if (!stickerData.rotation.isEmpty())
                        stickerBuilder.setRotation(Float.parseFloat(stickerData.rotation));
                    if (!stickerData.x.isEmpty())
                        stickerBuilder.setOffsetX(Float.parseFloat(stickerData.x));
                    if (!stickerData.y.isEmpty())
                        stickerBuilder.setOffsetY(Float.parseFloat(stickerData.y));
                    econBuilder.addStickers(stickerBuilder.build());
                    gencode.append(stickerData.name).append(" ").append(stickerData.wear).append(" ");
                }else {
                    gencode.append("0 0 ");
                }
            }
            //是否有挂件
            if(!Objects.equals(requestData.charm.name,"")){
                Econ.CEconItemPreviewDataBlock.Sticker.Builder stickerBuilder = Econ.CEconItemPreviewDataBlock.Sticker.newBuilder();
                stickerBuilder.setStickerId(Integer.parseInt(requestData.charm.name));
                if(!requestData.charm.pattern.isEmpty())stickerBuilder.setPattern(Integer.parseInt(requestData.charm.pattern));
                if(!requestData.charm.x.isEmpty())stickerBuilder.setOffsetX(Float.parseFloat(requestData.charm.x));
                if(!requestData.charm.z.isEmpty())stickerBuilder.setOffsetZ(Float.parseFloat(requestData.charm.z));
                if(!requestData.charm.highlight.isEmpty())stickerBuilder.setHighlightReel(Integer.parseInt(requestData.charm.highlight));
                econBuilder.addKeychains(stickerBuilder.build());
                gencode.append(requestData.charm.name).append(" ").append(requestData.charm.pattern);
            }else {
                gencode.append("0 0 ");
            }

            Econ.CEconItemPreviewDataBlock econInstance = econBuilder.build();
            byte[] serialized = econInstance.toByteArray();
            int protoSize = serialized.length;
            ByteBuffer buffer = ByteBuffer.allocate(1 + protoSize + 4);
            buffer.order(ByteOrder.BIG_ENDIAN);

            buffer.put((byte) 0x00); // null byte prefix
            buffer.put(serialized); // protobuf bytes

            // CRC32 over the first part (0x00 + serialized)
            CRC32 crc32 = new CRC32();
            crc32.update(buffer.array(), 0, 1 + protoSize);
            long crc = crc32.getValue();

            // XOR: (crc lower 16 bits) ^ (protoSize * crc)
            long xoredCrc = (crc & 0xFFFFL) ^ ((long) protoSize * crc);
            int xoredCrcInt = (int) (xoredCrc & 0xFFFFFFFFL); // Ensure 4-byte limit

            // Append the 4-byte CRC (big-endian)
            buffer.putInt(xoredCrcInt);

            // Convert to hex (uppercase)
            byte[] finalBytes = buffer.array();
            StringBuilder hexBuilder = new StringBuilder(finalBytes.length * 2);
            for (byte b : finalBytes) {
                hexBuilder.append(String.format("%02X", b));
            }

            Map<String,Object> map = new HashMap<>();
            map.put("inspectCode", "csgo_econ_action_preview " + hexBuilder);
            map.put("genCode", gencode.toString());
            gencode.setLength(0);
            return getSuccessResponseVO(map);
        }catch (Exception e){
            return getServerErrorResponseVO(e.getMessage());
        }
    }
}
