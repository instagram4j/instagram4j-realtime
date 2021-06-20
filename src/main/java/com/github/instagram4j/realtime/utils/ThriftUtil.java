package com.github.instagram4j.realtime.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TField;
import org.apache.thrift.protocol.TList;
import org.apache.thrift.protocol.TMap;
import org.apache.thrift.protocol.TStruct;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TIOStreamTransport;

public class ThriftUtil {
    public static byte[] serialize(Object o)
            throws IllegalArgumentException, IllegalAccessException, TException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TCompactProtocol tProto = new TCompactProtocol(new TIOStreamTransport(baos));

        serialize(o, tProto);

        return baos.toByteArray();
    }

    private static void serialize(Object o, TCompactProtocol tProto)
            throws IllegalArgumentException, IllegalAccessException, TException {
        List<Field> fields = Stream.of(o.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(ThriftField.class) != null)
                .collect(Collectors.toList());

        for (Field field : fields) {
            ThriftField thriftId = field.getAnnotation(ThriftField.class);
            field.setAccessible(true);
            Object valueObject = field.get(o);
            TField tField = new TField(field.getName(), getFieldType(field.getType()),
                    (short) thriftId.id());
            tProto.writeFieldBegin(tField);
            if (!writeType(valueObject, tProto)) {
                tProto.writeStructBegin(new TStruct(field.getName()));
                serialize(valueObject, tProto);
                tProto.writeStructEnd();
            }
            tProto.writeFieldEnd();
        }
        tProto.writeFieldStop();
    }

    private static boolean writeType(Object valueObject, TCompactProtocol tProto)
            throws TException {
        Class<?> type = valueObject.getClass();
        if (type.isAssignableFrom(String.class)) {
            tProto.writeString((String) valueObject);
        } else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(Long.TYPE)) {
            tProto.writeI64((long) valueObject);
        } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Integer.TYPE)) {
            tProto.writeI32((int) valueObject);
        } else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(Short.TYPE)) {
            tProto.writeI16((short) valueObject);
        } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(Byte.TYPE)) {
            tProto.writeByte((byte) valueObject);
        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Boolean.TYPE)) {
            tProto.writeBool((boolean) valueObject);
        } else if (type.isArray()) {
            // currently fixed to only int32 list type
            // TOOD: Change to different byte type is 0x05 for int32 arrays
            tProto.writeListBegin(new TList(TType.I32, Array.getLength(valueObject)));
            for (Object arr_obj : (Object[]) valueObject) {
                writeType(arr_obj, tProto);
            }
            tProto.writeListEnd();
        } else if (type.isAssignableFrom(HashMap.class)) {
            HashMap<Object, Object> map = (HashMap<Object, Object>) valueObject;
            // fixed map of binary binary (key and val are of type string)
            // TODO: Change to accommodate different types
            tProto.writeMapBegin(new TMap(TType.STRING, TType.STRING, map.entrySet().size()));
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                tProto.writeString((String) entry.getKey());
                tProto.writeString((String) entry.getValue());
            }
            tProto.writeMapEnd();
        } else {
            // assumption: this is a struct
            // TODO: May not be a struct in all cases
            return false;
        }

        return true;
    }

    public static <T> T deserialize(byte[] arr, Class<T> clazz) throws InstantiationException,
            IllegalAccessException, IllegalArgumentException, TException {
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        TCompactProtocol tProto = new TCompactProtocol(new TIOStreamTransport(bais));

        return deserialize(tProto, clazz);
    }

    private static <T> T deserialize(TCompactProtocol tProto, Class<T> clazz)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            TException {
        T t = clazz.newInstance();
        Field[] fields = t.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            tProto.readFieldBegin();
            Object val = readType(field.getType(), tProto);
            if (val != null) {
                field.set(t, val);
            } else {
                tProto.readStructBegin();
                field.set(t, deserialize(tProto, field.getType()));
                tProto.readStructEnd();
            }
            tProto.readFieldEnd();
        }

        return t;
    }

    public static Object readType(Class<?> type, TCompactProtocol tProto) throws TException {
        if (type.isAssignableFrom(String.class)) {
            return tProto.readString();
        } else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(Long.TYPE)) {
            return tProto.readI64();
        } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Integer.TYPE)) {
            return tProto.readI32();
        } else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(Short.TYPE)) {
            return tProto.readI16();
        } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(Byte.TYPE)) {
            return tProto.readByte();
        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Boolean.TYPE)) {
            return tProto.readBool();
        } else if (type.isArray()) {
            TList tList = tProto.readListBegin();
            // fixed deserializing list of int32
            // TODO: change to deserialize any type
            Object arr = Array.newInstance(Integer.class, tList.size);
            for (int i = 0; i < tList.size; ++i) {
                Array.set(arr, i, readType(Integer.class, tProto));
            }
            tProto.readListEnd();

            return arr;
        } else if (type.isAssignableFrom(HashMap.class)) {
            // fixed deserializing a map of string key and value
            // TODO: change to deserialize map of any key, val of any type
            TMap tMap = tProto.readMapBegin();
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i <= tMap.size; ++i) {
                String key = tProto.readString();
                String value = tProto.readString();
                map.put(key, value);
            }
            tProto.readMapEnd();
            
            return map;
        } else {
            return null;
        }
    }

    public static byte getFieldType(Class<?> type) {
        if (type.isAssignableFrom(String.class)) {
            return TType.STRING;
        } else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(Long.TYPE)) {
            return TType.I64;
        } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Integer.TYPE)) {
            return TType.I32;
        } else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(Short.TYPE)) {
            return TType.I16;
        } else if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(Byte.TYPE)) {
            return TType.BYTE;
        } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Boolean.TYPE)) {
            return TType.BOOL;
        } else if (type.isArray()) {
            return TType.LIST;
        } else if (type.isAssignableFrom(Map.class)) {
            return TType.MAP;
        } else {
            return TType.STRUCT;
        }
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface ThriftField {
        public int id();
    }


}
