/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.operator.scalar;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Primitives;
import io.airlift.slice.Slice;
import io.prestosql.annotation.UsedByGeneratedCode;
import io.prestosql.metadata.BoundVariables;
import io.prestosql.metadata.Metadata;
import io.prestosql.metadata.ResolvedFunction;
import io.prestosql.metadata.SqlOperator;
import io.prestosql.spi.PrestoException;
import io.prestosql.spi.block.Block;
import io.prestosql.spi.block.SingleMapBlock;
import io.prestosql.spi.connector.ConnectorSession;
import io.prestosql.spi.type.Type;
import io.prestosql.spi.type.TypeSignature;
import io.prestosql.sql.InterpretedFunctionInvoker;

import java.lang.invoke.MethodHandle;

import static io.prestosql.metadata.Signature.typeVariable;
import static io.prestosql.operator.scalar.ScalarFunctionImplementation.ArgumentProperty.valueTypeArgumentProperty;
import static io.prestosql.operator.scalar.ScalarFunctionImplementation.NullConvention.RETURN_NULL_ON_NULL;
import static io.prestosql.spi.StandardErrorCode.INVALID_FUNCTION_ARGUMENT;
import static io.prestosql.spi.function.OperatorType.SUBSCRIPT;
import static io.prestosql.spi.type.TypeSignature.mapType;
import static io.prestosql.spi.type.TypeUtils.readNativeValue;
import static io.prestosql.spi.type.VarcharType.VARCHAR;
import static io.prestosql.util.Reflection.methodHandle;
import static java.lang.String.format;

public class MapSubscriptOperator
        extends SqlOperator
{
    private static final MethodHandle METHOD_HANDLE_BOOLEAN = methodHandle(MapSubscriptOperator.class, "subscript", MissingKeyExceptionFactory.class, Type.class, Type.class, ConnectorSession.class, Block.class, boolean.class);
    private static final MethodHandle METHOD_HANDLE_LONG = methodHandle(MapSubscriptOperator.class, "subscript", MissingKeyExceptionFactory.class, Type.class, Type.class, ConnectorSession.class, Block.class, long.class);
    private static final MethodHandle METHOD_HANDLE_DOUBLE = methodHandle(MapSubscriptOperator.class, "subscript", MissingKeyExceptionFactory.class, Type.class, Type.class, ConnectorSession.class, Block.class, double.class);
    private static final MethodHandle METHOD_HANDLE_OBJECT = methodHandle(MapSubscriptOperator.class, "subscript", MissingKeyExceptionFactory.class, Type.class, Type.class, ConnectorSession.class, Block.class, Object.class);

    public MapSubscriptOperator()
    {
        super(SUBSCRIPT,
                ImmutableList.of(typeVariable("K"), typeVariable("V")),
                ImmutableList.of(),
                new TypeSignature("V"),
                ImmutableList.of(mapType(new TypeSignature("K"), new TypeSignature("V")), new TypeSignature("K")),
                true);
    }

    @Override
    public ScalarFunctionImplementation specialize(BoundVariables boundVariables, int arity, Metadata metadata)
    {
        Type keyType = boundVariables.getTypeVariable("K");
        Type valueType = boundVariables.getTypeVariable("V");

        MethodHandle methodHandle;
        if (keyType.getJavaType() == boolean.class) {
            methodHandle = METHOD_HANDLE_BOOLEAN;
        }
        else if (keyType.getJavaType() == long.class) {
            methodHandle = METHOD_HANDLE_LONG;
        }
        else if (keyType.getJavaType() == double.class) {
            methodHandle = METHOD_HANDLE_DOUBLE;
        }
        else {
            methodHandle = METHOD_HANDLE_OBJECT;
        }
        MissingKeyExceptionFactory missingKeyExceptionFactory = new MissingKeyExceptionFactory(metadata, keyType);
        methodHandle = methodHandle.bindTo(missingKeyExceptionFactory).bindTo(keyType).bindTo(valueType);
        methodHandle = methodHandle.asType(methodHandle.type().changeReturnType(Primitives.wrap(valueType.getJavaType())));

        return new ScalarFunctionImplementation(
                true,
                ImmutableList.of(
                        valueTypeArgumentProperty(RETURN_NULL_ON_NULL),
                        valueTypeArgumentProperty(RETURN_NULL_ON_NULL)),
                methodHandle);
    }

    @UsedByGeneratedCode
    public static Object subscript(MissingKeyExceptionFactory missingKeyExceptionFactory, Type keyType, Type valueType, ConnectorSession session, Block map, boolean key)
    {
        SingleMapBlock mapBlock = (SingleMapBlock) map;
        int valuePosition = mapBlock.seekKeyExact(key);
        if (valuePosition == -1) {
            throw missingKeyExceptionFactory.create(session, key);
        }
        return readNativeValue(valueType, mapBlock, valuePosition);
    }

    @UsedByGeneratedCode
    public static Object subscript(MissingKeyExceptionFactory missingKeyExceptionFactory, Type keyType, Type valueType, ConnectorSession session, Block map, long key)
    {
        SingleMapBlock mapBlock = (SingleMapBlock) map;
        int valuePosition = mapBlock.seekKeyExact(key);
        if (valuePosition == -1) {
            throw missingKeyExceptionFactory.create(session, key);
        }
        return readNativeValue(valueType, mapBlock, valuePosition);
    }

    @UsedByGeneratedCode
    public static Object subscript(MissingKeyExceptionFactory missingKeyExceptionFactory, Type keyType, Type valueType, ConnectorSession session, Block map, double key)
    {
        SingleMapBlock mapBlock = (SingleMapBlock) map;
        int valuePosition = mapBlock.seekKeyExact(key);
        if (valuePosition == -1) {
            throw missingKeyExceptionFactory.create(session, key);
        }
        return readNativeValue(valueType, mapBlock, valuePosition);
    }

    @UsedByGeneratedCode
    public static Object subscript(MissingKeyExceptionFactory missingKeyExceptionFactory, Type keyType, Type valueType, ConnectorSession session, Block map, Object key)
    {
        SingleMapBlock mapBlock = (SingleMapBlock) map;
        int valuePosition = mapBlock.seekKeyExact(key);
        if (valuePosition == -1) {
            throw missingKeyExceptionFactory.create(session, key);
        }
        return readNativeValue(valueType, mapBlock, valuePosition);
    }

    private static class MissingKeyExceptionFactory
    {
        private final InterpretedFunctionInvoker functionInvoker;
        private final ResolvedFunction castFunction;

        public MissingKeyExceptionFactory(Metadata metadata, Type keyType)
        {
            functionInvoker = new InterpretedFunctionInvoker(metadata);

            ResolvedFunction castFunction = null;
            try {
                castFunction = metadata.getCoercion(keyType, VARCHAR);
            }
            catch (PrestoException ignored) {
            }
            this.castFunction = castFunction;
        }

        public PrestoException create(ConnectorSession session, Object value)
        {
            if (castFunction != null) {
                try {
                    Slice varcharValue = (Slice) functionInvoker.invoke(castFunction, session, value);
                    return new PrestoException(INVALID_FUNCTION_ARGUMENT, format("Key not present in map: %s", varcharValue.toStringUtf8()));
                }
                catch (RuntimeException ignored) {
                }
            }
            return new PrestoException(INVALID_FUNCTION_ARGUMENT, "Key not present in map");
        }
    }
}
