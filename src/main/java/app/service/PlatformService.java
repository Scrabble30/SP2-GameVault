package app.service;

import app.dto.PlatformDTO;

import java.util.Set;

public interface PlatformService {

    Set<PlatformDTO> getPlatforms();
}
