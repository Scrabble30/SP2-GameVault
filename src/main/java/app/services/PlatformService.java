package app.services;

import app.dtos.PlatformDTO;

import java.util.Set;

public interface PlatformService {

    Set<PlatformDTO> getPlatforms();
}
