package com.springcore.ai.scaiplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // หัวข้อการแจ้งเตือน เช่น "New Document Request"
    private String title;

    // ข้อความรายละเอียด เช่น "คุณมีรายการรออนุมัติหมายเลข DOC2026-001"
    private String message;

    // ประเภทการแจ้งเตือน (สำหรับแยกสีใน UI) เช่น "INFO", "SUCCESS", "WARN", "ERROR"
    private String type;

    // ID ของเอกสารที่เกี่ยวข้อง เพื่อให้ Frontend ใช้เปิดหน้าเอกสาร
    private Long parentId;

    // วันที่เกิดเหตุการณ์ (พ่นออกไปเป็น Timestamp ตาม JacksonConfig ของคุณ)
    private Date timestamp;

    // ข้อมูลเพิ่มเติมในรูปแบบ JSON string (เผื่อขยายในอนาคต)
    private String payload;

    private String url;

}