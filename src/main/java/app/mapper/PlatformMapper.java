package app.mapper;

import app.config.MapperConfig;
import app.dto.PlatformDTO;
import app.entity.Platform;
import org.modelmapper.ModelMapper;

public class PlatformMapper {

    private final ModelMapper modelMapper;

    public PlatformMapper() {
        modelMapper = MapperConfig.getInstance().getModelMapper();
    }

    public PlatformDTO convertToDTO(Platform platform) {
        return modelMapper.map(platform, PlatformDTO.class);
    }

    public Platform convertToEntity(PlatformDTO platformDTO) {
        return modelMapper.map(platformDTO, Platform.class);
    }
}
