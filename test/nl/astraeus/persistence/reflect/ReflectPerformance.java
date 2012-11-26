package nl.astraeus.persistence.reflect;

import nl.astraeus.persistence.model.Company;
import nl.astraeus.persistence.model.Employee;
import nl.astraeus.util.Util;
import org.junit.Ignore;

/**
 * User: rnentjes
 * Date: 11/26/12
 * Time: 9:42 PM
 * <p/>
 * (c) Astraeus B.V.
 */
@Ignore
public class ReflectPerformance {

    public static void main(String [] args) {
        new ReflectPerformance();
    }

    public ReflectPerformance() {
        String name, companyName, result1="", result2="";
        int j = 0;
        Employee e = new Employee(1, "Rien", new Company(1, "Company"));

        long start1 = System.nanoTime();

        name = String.valueOf(ReflectHelper.get().getFieldValue(e, "name"));
        companyName = String.valueOf(ReflectHelper.get().getFieldValue(e, "company", "name"));

        long start2 = System.nanoTime();

        name = String.valueOf(ReflectHelper.get().getFieldValue(e, "name"));
        companyName = String.valueOf(ReflectHelper.get().getFieldValue(e, "company", "name"));

        long start3 = System.nanoTime();

        for (int i = 0; i < 100; i++) {
            name = String.valueOf(ReflectHelper.get().getFieldValue(e, "name"));
            companyName = String.valueOf(ReflectHelper.get().getFieldValue(e, "company", "name"));

            result1 = name;
            result2 = companyName;

            j++;
        }

        System.out.println(result1);
        System.out.println(result2);

        System.out.println("First took " + Util.formatNano(start2 - start1) + "ms");
        System.out.println("Second took " + Util.formatNano(start3 - start2) + "ms");
        System.out.println(Integer.valueOf(j) + " iterations took " + Util.formatNano(System.nanoTime() - start3) + "ms");

    }
}
