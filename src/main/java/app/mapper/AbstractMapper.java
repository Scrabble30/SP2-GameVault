package app.mapper;

import app.config.MapperConfig;
import org.modelmapper.ModelMapper;

public class AbstractMapper<D, E> implements Mapper<D, E> {

    protected final ModelMapper modelMapper;
    protected final Class<D> dtoClass;
    protected final Class<E> entityClass;

    protected AbstractMapper(Class<D> dtoClass, Class<E> entityClass) {
        modelMapper = MapperConfig.getInstance().getModelMapper();

        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
    }

    @Override
    public D convertToDTO(E entity) {
        return modelMapper.map(entity, dtoClass);
    }

    @Override
    public E convertToEntity(D dto) {
        return modelMapper.map(dto, entityClass);
    }
}