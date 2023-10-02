package com.cst438.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
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
	
	@Autowired
	AssignmentGradeRepository assignmentGradeRepository;
	
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
	
	
	//create assignment (POST)
	@PostMapping("/assignment")
	public int createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
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
		
		//save and return the ID of the created assignment
		assignment = assignmentRepository.save(assignment);
		return assignment.getId();
	}
	
	
	//Retrieve assignment by primary key (GET)
	@GetMapping("/assignment/{id}")
	public AssignmentDTO getAssignment(@PathVariable("id") int id) {
		Assignment assignment = assignmentRepository.findById(id).orElse(null);
        
		//exception if no assignment with given ID is found.
		if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment ID not found");
        }
		
		//format the date in YYYY MM DD
		String formattedDate = assignment.getDueDate().toLocalDate().toString();

		//create an assignmentDTO object and set its fields based on the Assignment object
        AssignmentDTO assignmentDTO = new AssignmentDTO(
        		assignment.getId(),
        		assignment.getName(),
        		formattedDate,
        		assignment.getCourse().getTitle(),
        		assignment.getCourse().getCourse_id()
        		);

		//return AssignmentDTO object
		return assignmentDTO;
    }
	
	//Update (PUT)
	@PutMapping("/assignment/{id}")
	public void updateAssignment(@PathVariable("id") int id, @RequestBody AssignmentDTO assignmentDTO) {
		//check if assignment exists using the repository
		Assignment assignment = assignmentRepository.findById(id).orElse(null);
        
		//throw exception if assignment doesn't exists
		if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }

        assignment.setName(assignmentDTO.assignmentName());
        assignment.setDueDate(java.sql.Date.valueOf(assignmentDTO.dueDate()));
        
        //check if course exists
        Course course = courseRepository.findById(assignmentDTO.courseId()).orElse(null);
        
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found, try another one");
        }
        
        //set the course for the assignment
        assignment.setCourse(course);
        
        //save the updated assignment
        assignmentRepository.save(assignment);
    }
	
	//delete (DELETE)
	//delete /assignment/{id}
	//delete /assignment/{id}?force=yes
	@DeleteMapping("/assignment/{id}")
	public void deleteAssignment(@PathVariable int id, @RequestParam("force") Optional<String> force) {
		//check if assignment exists with repository or return an error message
		Assignment assignment = assignmentRepository.findById(id).orElse(null);
	     
		if (assignment == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
	    }
		//get assignment grades
		Iterable<AssignmentGrade> grades = assignmentGradeRepository.findAll();
		//check
		boolean hasGrades=false;
		for (AssignmentGrade grade : grades) {
			if (grade.getAssignment().getId() == id) {
				hasGrades=true;
				break;
			}
		}
		//if assignment has grades and no parameter is used
		if(hasGrades && force.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Assignment has grades!, if you want to continue use force parameter");
		}
		//delete the assignment
	    assignmentRepository.delete(assignment);
	    
	    
		}
}
