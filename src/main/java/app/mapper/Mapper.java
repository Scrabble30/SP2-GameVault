package app.mapper;

public interface Mapper<D, E> {

    D convertToDTO(E entity);

    E convertToEntity(D dto);

}
