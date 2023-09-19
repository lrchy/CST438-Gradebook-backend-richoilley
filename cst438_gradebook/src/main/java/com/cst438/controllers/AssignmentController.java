package com.cst438.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin 
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(), 
					as.getName(), 
					as.getDueDate().toString(), 
					as.getCourse().getTitle(), 
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	
	//create assignment (POST requests)
	@PostMapping("/assignment")
	public Assignment createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
		//first convert the AssignmentDTO to Assignment entity
		Assignment assignment = new Assignment();
		assignment.setName(assignmentDTO.assignmentName());
		assignment.setDueDate(java.sql.Date.valueOf(assignmentDTO.dueDate()));
		
		//now we find the course associated with this assignment and we set it
		Course course = courseRepository.findById(assignmentDTO.courseId()).orElse(null);
		if (course == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found, try another one");
		}
		assignment.setCourse(course);
		
		//save and return the created assignment
		return assignmentRepository.save(assignment);
	}
	
	//Retrieve a single assignment (GET)
	@GetMapping("/assignment/{id}")
	public Assignment getAssignmentById(@PathVariable int id) {
		Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment ID not found");
        }

        return assignment;
    }
	
	//Update (PUT)
	@PutMapping("/assignment/{id}")
	public Assignment updateAssignment(@PathVariable int id, @RequestBody AssignmentDTO assignmentDTO) {
		//check if assignment exists
		Assignment assignment = assignmentRepository.findById(id).orElse(null);
        if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }

        assignment.setName(assignmentDTO.assignmentName());
        assignment.setDueDate(java.sql.Date.valueOf(assignmentDTO.dueDate()));
        
        Course course = courseRepository.findById(assignmentDTO.courseId()).orElse(null);
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found, try another one");
        }
        assignment.setCourse(course);

        return assignmentRepository.save(assignment);
    }
	
	//Delete (DELETE)
	@DeleteMapping("/assignment/{id}")
	public void deleteAssignment(@PathVariable int id) {
		//check if assignment exists or return an error message
		 Assignment assignment = assignmentRepository.findById(id).orElse(null);
	        if (assignment == null) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
	        }

	        assignmentRepository.delete(assignment);
	    }
	
	//TODO get assignment grades method
}
