package app.mapper.impl;

import app.dto.PlatformDTO;
import app.entity.Platform;
import app.mapper.AbstractMapper;

public class PlatformMapperImpl extends AbstractMapper<PlatformDTO, Platform> {

    private static PlatformMapperImpl instance;

    private PlatformMapperImpl(Class<PlatformDTO> dtoClass, Class<Platform> entityClass) {
        super(dtoClass, entityClass);
    }

    public static PlatformMapperImpl getInstance() {
        if (instance == null) {
            instance = new PlatformMapperImpl(PlatformDTO.class, Platform.class);
        }

        return instance;
    }
}
