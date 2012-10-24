package nl.astraeus.persistence.reflect;

import org.junit.Ignore;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * User: rnentjes
 * Date: 7/8/12
 * Time: 8:20 PM
 */
@Ignore
public class ShowFields {

    public static void main(String [] args) throws Exception {
        ShowFields sf = new ShowFields();

        sf.showFields(new Date());
    }

    public void showFields(Object o) {
        System.out.println("Showing field of: "+o.getClass());

        for (Field field : ReflectHelper.get().getFieldsFromClass(o.getClass())) {
            System.out.println("Field: " + field.getName() + ", " + field.getType());
        }

        System.out.println("*** Persistable ***");

        for (Field field : ReflectHelper.get().getPersistableFieldsFromClass(o.getClass())) {
            System.out.println("Field: " + field.getName() + ", " + field.getType());
        }
    }
}
