package com.tastik.cycal.data.rest.dtos;

import java.util.List;

public record ResponseDTO(
        List<MonthDTO> items
) {
}
