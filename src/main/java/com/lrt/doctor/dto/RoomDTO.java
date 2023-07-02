package com.lrt.doctor.dto;

import com.lrt.doctor.entity.Room;
import lombok.Data;

@Data
public class RoomDTO extends Room {

    private String deparName;

}
