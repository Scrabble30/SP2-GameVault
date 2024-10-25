package app.mapper;

import app.config.MapperConfig;
import app.dto.PlatformDTO;
import app.entity.Platform;

public class PlatformMapper {

    public PlatformDTO convertToDTO(Platform platform) {
        return MapperConfig.getModelMapper().map(platform, PlatformDTO.class);
    }

    public Platform convertToEntity(PlatformDTO platformDTO) {
        return MapperConfig.getModelMapper().map(platformDTO, Platform.class);
    }
}
