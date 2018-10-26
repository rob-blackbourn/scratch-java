package net.jetblack.util.types;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.logging.Logger;

public abstract class TypeLiteral<T> {
   
	private static final Logger logger = Logger.getLogger("com.glgpartners.TypeLiteral<T>");
	
   protected final Type type;

   protected TypeLiteral() {
       Class<?> clazz = this.getClass();
       // The generic superclass will be this class unless the
       // inheritance hierarchy is even deeper! 
       Type superClass = clazz.getGenericSuperclass();
       if (superClass instanceof ParameterizedType) {
           this.type = getComponentType(superClass);
           
       // If sub-classes are not generic...
       } else {
           throw new IllegalArgumentException("Class is not generic: " + clazz);
       }
   }

   private TypeLiteral(Type type) {
       this.type = getComponentType(type);
   }

   public TypeLiteral(TypeLiteral<T> typeLiteral) {
       this.type = typeLiteral.type;
   }
   
   public static <S> TypeLiteral<?> create(Class<S> clazz) {
       // S is needed to construct an instance, cannot use wild-card.
       return new TypeLiteral<S>(clazz.getGenericSuperclass()){/*magic*/}; 
   }

   public static TypeLiteral<?> create(Object object) {
       return create(object.getClass()); 
   }
   
   public static TypeLiteral<?> create(Type type) {
       return new TypeLiteral<Object>(type){/*magic*/};
   }

   public Class<T> asClass() {
       Class<?> clazz = this.getRawType();
       if (clazz == null) {
           return null;
       }
       // See JavaDoc above...
       @SuppressWarnings("unchecked") Class<T> c = (Class<T>)clazz;
       return c; 
   }

   public Class<T> asClass(Class<?> clazz) {
       return this.asType(create(clazz)).asClass();
   }
   
   public <S> TypeLiteral<T> asType(TypeLiteral<S> typeLiteral) {
       if ((this.type instanceof TypeVariable) || (this.type instanceof WildcardType)) {
           throw new ClassCastException();
       }
       // Invariant: all other types have a raw type!
       
       // Raw type...
       if (isAssignableFrom(this.getRawType(), typeLiteral.getRawType(), false)) {
           // Type parameters, if any...
           if (isAssignableFrom(this.type, typeLiteral.type, true)) {
               // Known to succeed because of the above checks...
               @SuppressWarnings("unchecked") TypeLiteral<T> tl = (TypeLiteral<T>)typeLiteral;
               return tl;
           }
       }
       throw new ClassCastException(this.toString() + " !> " + typeLiteral);
   }

   private final static boolean isAssignableFrom(Type a, Type b, boolean equal) {
       if (a instanceof TypeVariable) {
           return false;
       } else if (a instanceof Class) {
           if (b instanceof Class) {
               if (equal) {
                   return a.equals(b);
               } else {
                   return ((Class<?>)a).isAssignableFrom((Class<?>)b);
               }
           } else if (b instanceof ParameterizedType) {
               return isAssignableFrom(a, getComponentType(b), true);
           }
       } else if (a instanceof ParameterizedType) {
           if (b instanceof ParameterizedType) {
               ParameterizedType aa = (ParameterizedType)a;
               ParameterizedType bb = (ParameterizedType)b;
               if (isAssignableFrom(getRawType(aa), getRawType(bb), false)) {
                   return isAssignableFrom(getComponentType(aa), getComponentType(bb), true);
               }
           } else if (b instanceof Class) {
               return isAssignableFrom(a, ((Class<?>)b).getGenericSuperclass(), true);
           }
       } else if (a instanceof GenericArrayType) {
           if (b instanceof GenericArrayType) {
               return isAssignableFrom(getComponentType(a), getComponentType(b), true);
           }
       } else if (a instanceof WildcardType) {
           WildcardType wt = (WildcardType)a;
           for (Type t : wt.getLowerBounds()) {
               if (!isAssignableFrom(b, t, false)) {
                   return false;
               }
           }
           for (Type t : wt.getUpperBounds()) {
               if (!isAssignableFrom(t, b, false)) {
                   return false;
               }
           }
           return true;
       }
       return false;
   }
   
   public Type getType() {
       return this.type;
   }
   
   public static Type getComponentType(Type type) {
       // Parameterised type...
       if (type instanceof ParameterizedType) {
           Type[] types = ((ParameterizedType)type).getActualTypeArguments();
           if (types.length == 0) {
               throw new IllegalArgumentException("No type parameters: " + type);
           } else if (types.length > 1) {
        	   logger.fine("Using first type parameter of: " + types);
           }
           return types[0]; // use first
           
       // Generic array type - recurse... 
       } else if (type instanceof GenericArrayType) {
           return getComponentType(((GenericArrayType)type).getGenericComponentType());
           
       } else if (type == null) {
           throw new NullPointerException("Type cannot be null");
       }
       
       return type;
   }
   
   public Class<?> getRawType() {
       return getRawType(this.type);
   }
   
   public static Class<?> getRawType(Type type) {
       // Class...
       if (type instanceof Class) {
           return (Class<?>)type;
           
       // Parameterised type...
       } if (type instanceof ParameterizedType) {
           ParameterizedType pt = (ParameterizedType)type;
           return (Class<?>)pt.getRawType(); // class, enum, or interface
           
       // Generic array type - recurse...
       } else if (type instanceof GenericArrayType) {
           return getRawType(((GenericArrayType)type).getGenericComponentType());
           
       } else if (type == null) {
           throw new NullPointerException("Type cannot be null");
       }
       // No raw type could be established...
       return null;
   }
   
   public int hashCode() {
       return this.type.hashCode();
   }
   
   public boolean equals(Object object) {
       if (!(object instanceof TypeLiteral)) {
           return false;
       }
       TypeLiteral<?> tl = (TypeLiteral<?>)object;
       return this.type.equals(tl.type);
   }

   public String toString() {
       return this.type.toString();
   }
   
}