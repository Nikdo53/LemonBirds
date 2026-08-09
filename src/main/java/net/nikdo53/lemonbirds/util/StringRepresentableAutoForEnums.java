package net.nikdo53.lemonbirds.util;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

//"borrowed" from wd
public interface StringRepresentableAutoForEnums extends StringRepresentable {
    @Override
    default String getSerializedName()
    {
        return ((Enum<?>) this).name().toLowerCase(Locale.ROOT);
    }
}