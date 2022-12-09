package net.iseryproject.core.util.json;


import net.evilblock.pidgin.shaded.com.google.gson.JsonObject;

public interface JsonSerializer<T> {

	JsonObject serialize(T t);

}
