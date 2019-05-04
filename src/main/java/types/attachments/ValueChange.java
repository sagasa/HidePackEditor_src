package types.attachments;

import java.lang.reflect.Field;
import java.util.List;

import types.base.DataBase;

public class ValueChange extends DataBase {

	public static final String SPLIT = "\\.";

	public String TARGET;
	public ChangeType TYPE;
	public Object VALUE;

	public ValueChange() {

	}

	public ValueChange(String target, ChangeType type, Object value) {
		TARGET = target;
		TYPE = type;
		VALUE = value;
	}

	public void apply(DataBase data) {
		apply(data, TARGET);
	}

	@SuppressWarnings("unchecked")
	private void apply(DataBase data, String target) {
		try {
			//階層化されているか
			String[] split = target.split(SPLIT, 2);
			Field field = data.getClass().getField(split[0]);
			if (split.length > 1) {
				apply((DataBase) field.get(data), split[1]);
				return;
			}
			Class<?> type = null;
			Class<?> clazz = field.getType();
			if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
				type = int.class;
			} else if (float.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz)) {
				type = float.class;
			}
			switch (TYPE) {
			case ADD_LIST_STRING:
				((List<String>) field.get(data)).add((String) VALUE);
				break;
			case REMOVE_LIST_STRING:
				((List<String>) field.get(data)).remove((String) VALUE);
				break;
			case ADD_NUMBER:
				if (type == int.class)
					field.setInt(data, (int) (field.getInt(data) + (double) VALUE));
				else if (type == float.class)
					field.setFloat(data, (float) (field.getFloat(data) + (double) VALUE));
				break;
			case DIA_NUMBER:
				if (type == int.class)
					field.setInt(data, (int) (field.getInt(data) * (double) VALUE));
				else if (type == float.class)
					field.setFloat(data, (float) (field.getFloat(data) + (double) VALUE));
				break;
			case SET_NUMBER:
				if (type == int.class)
					field.setInt(data, (int) ((double) VALUE));
				else if (type == float.class)
					field.setFloat(data, (float) ((double) VALUE));

				break;
			default:
				break;

			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return TARGET + " " + TYPE + " " + VALUE + super.toString();
	}

	private static final int STRING = 0;
	private static final int NUMBER = 1;

	public enum ChangeType {

		ADD_NUMBER(NUMBER), DIA_NUMBER(NUMBER), SET_NUMBER(NUMBER), ADD_LIST_STRING(STRING), REMOVE_LIST_STRING(STRING);
		private ChangeType(int type) {
			valueType = type;
		}

		int valueType;
	}
}
