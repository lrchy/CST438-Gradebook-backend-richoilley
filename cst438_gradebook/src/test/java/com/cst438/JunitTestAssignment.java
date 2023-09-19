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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyInt;
import java.util.Optional;

import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.AssignmentRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestAssignment {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private AssignmentRepository assignmentRepository;
	    
	@MockBean
	private CourseRepository courseRepository;
	
	@Autowired
	//converts objects into JSON and JSON into objects
	private ObjectMapper objectMapper;
	
	@Test
	public void listAssignments() throws Exception{
		MockHttpServletResponse response = 
				mvc.perform(get("/assignment"))
                .andReturn()
                .getResponse();
		
		assertEquals(200, response.getStatus());
		assertTrue(response.getContentType().contains(MediaType.APPLICATION_JSON_VALUE));
	}
	
	@Test
	public void addAssignment() throws Exception{
		//Define a new AssignmentDTO object to add:
		AssignmentDTO newAssignment = new AssignmentDTO(3, "New Assignment", "2023-09-03", "test course", 31045);
		Course course = new Course();
        course.setCourse_id(31045);
        course.setTitle("test course");
        when(courseRepository.findById(anyInt())).thenReturn(Optional.of(course));

        // make POST request and assert the response
        mvc.perform(post("/assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAssignment)))
                .andExpect(status().isOk());
    }
	
	@Test
	public void updateAssignment() throws Exception {
	    // Create a new AssignmentDTO object with the details of the assignment to be updated.
	    AssignmentDTO updatedAssignment = new AssignmentDTO(1, "Updated Assignment", "2023-09-18", "test course", 31045);

	    // make PUT request and assert the response
	    mvc.perform(put("/assignment/1")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(updatedAssignment)))
	    		.andExpect(status().isOk());
	}
	
	//TODO delete assignment with and without FORCE
}
