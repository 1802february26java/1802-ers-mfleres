import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.repository.EmployeeJDBS;

public class ERSTest {
	private static final int TEST_ID = 20;
	
	@Test
	public void testSelectEmployee() {
		Employee testEmployee = new Employee(TEST_ID, "Amanda", "Fleres", "afleres", "c4tp4ssw0rd", "catfleres@gmail.com", new EmployeeRole(1,"Employee"));
		assertNotEquals(testEmployee, null);
		System.out.println(testEmployee);
		EmployeeJDBS.getInstance().insert(testEmployee);
		Employee storedEmployee = EmployeeJDBS.getInstance().select(TEST_ID);
		assertNotEquals(storedEmployee, null);
		System.out.println(storedEmployee);
		assertEquals(testEmployee.getUsername(), storedEmployee.getUsername());
	}
	
	@Test
	public void testUpdateEmployee() {
		Employee storedEmployee = EmployeeJDBS.getInstance().select(TEST_ID);
		assertNotEquals(storedEmployee, null);
		System.out.println(storedEmployee);
		storedEmployee.setEmail("cat@gmail.com");
		EmployeeJDBS.getInstance().update(storedEmployee);
		Employee updatedEmployee = EmployeeJDBS.getInstance().select(TEST_ID);
		assertEquals(storedEmployee.getEmail(), updatedEmployee.getEmail());
	}
}
