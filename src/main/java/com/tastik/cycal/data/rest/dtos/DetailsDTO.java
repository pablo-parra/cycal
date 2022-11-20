package com.tastik.cycal.data.rest.dtos;

import com.tastik.cycal.core.domain.Details;

public record DetailsDTO(
        String title,
        String url,
        boolean isExternal
) {
    public Details toDomain() {
        return new Details(
                title,
                url,
                isExternal
        );
    }
}
