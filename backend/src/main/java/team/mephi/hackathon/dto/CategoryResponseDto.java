package team.mephi.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CategoryResponseDto {
    private UUID id;
    private String name;
}
