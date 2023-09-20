package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.AssignmentRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestAssignment {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private AssignmentRepository assignmentRepository;
	    
	@Autowired
	private CourseRepository courseRepository;
	
	private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	
	@Test
	public void listAssignments() throws Exception{
		//Setup (same for every test)
		Course course = new Course();
		course.setCourse_id(31045); //existing id, this is Database Systems course
		course.setTitle("test course");
		courseRepository.save(course);
		
		Assignment assignment = new Assignment();
		assignment.setName("Test Assignment");
		assignment.setDueDate(java.sql.Date.valueOf("2023-09-03"));
		assignment.setCourse(course);
		assignmentRepository.save(assignment);
		
		//Test
		MockHttpServletResponse response = 
				mvc.perform(get("/assignment"))
                .andReturn()
                .getResponse();
		
		assertEquals(200, response.getStatus());
		assertTrue(response.getContentType().contains(MediaType.APPLICATION_JSON_VALUE));
		
		//clean up setup assignment
		assignmentRepository.deleteAll();
		courseRepository.deleteAll();
		}
	
	@Test
	public void addAssignment() throws Exception{
		
		Course course = new Course();
		course.setCourse_id(31045); 
		course.setTitle("test course");
		courseRepository.save(course);
		
		AssignmentDTO newAssignment = new AssignmentDTO(0, "New Assignment", "2023-09-03", "test course", 31045);
		
        // make POST request and assert the response
        MockHttpServletResponse response = mvc.perform(post("/assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newAssignment)))
                .andReturn()
                .getResponse();
        
        //assert that response is OK
        assertEquals(200, response.getStatus());
        
        //parse response to get the ID of the new assignment
        int newAssignmentId = Integer.parseInt(response.getContentAsString());     
        //assert this ID is valid
        assertTrue(newAssignmentId > 0);
        
        //clean up
        assignmentRepository.deleteAll();
        courseRepository.deleteAll();
        }
	
	@Test
	public void updateAssignment() throws Exception {
		//setup
		Course course = new Course();
		course.setCourse_id(31045);
		course.setTitle("test course");
		courseRepository.save(course);
		
		Assignment assignment = new Assignment();
		assignment.setName("Test Assignment");
		assignment.setDueDate(java.sql.Date.valueOf("2023-09-03"));
		assignment.setCourse(course);
		assignmentRepository.save(assignment);
		
		//new updated assignment AssignmentDTO :
		AssignmentDTO updatedAssignment = new AssignmentDTO(assignment.getId(), "Updated Assignment", "2023-09-18", "test course", 31045);
		
	    // make PUT request and assert the response
	    mvc.perform(put("/assignment/" + assignment.getId())
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(asJsonString(updatedAssignment)))
	    		.andExpect(status().isOk());
	    
	    //Clean up
	    assignmentRepository.deleteAll();
	    courseRepository.deleteAll();
	}
	
	@Test
	public void deleteAssignment() throws Exception {
		Course course = new Course();
		course.setCourse_id(31045);
		course.setTitle("test course");
		courseRepository.save(course);
		
		Assignment assignment = new Assignment();
		assignment.setName("Test Assignment");
		assignment.setDueDate(java.sql.Date.valueOf("2023-09-03"));
		assignment.setCourse(course);
		assignmentRepository.save(assignment);
		
		MockHttpServletResponse response = mvc.perform(delete("/assignment/" + assignment.getId())
				.param("force","yes"))
				.andReturn()
				.getResponse();
	
		assertEquals(200, response.getStatus());
		
		//Clean up
		assignmentRepository.deleteAll();
		courseRepository.deleteAll();
	}
}
