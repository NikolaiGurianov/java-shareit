package ru.practicum.shareitserver.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;


@UtilityClass
public class Constant {
    public static final Sort SORT_BY_DESC = Sort.by(Sort.Direction.DESC, "start");
    public static final Sort SORT_BY_DESC_CREATED = Sort.by(Sort.Direction.DESC, "created");
}
