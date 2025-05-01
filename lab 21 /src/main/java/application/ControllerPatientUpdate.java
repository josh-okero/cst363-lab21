package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import application.model.Patient;
import application.model.PatientRepository;
import view.PatientView;

import java.util.Optional;

@Controller
public class ControllerPatientUpdate {

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;


    @GetMapping("/patient/edit/{id}")
    public String getUpdateForm(@PathVariable int id, Model model) {
        try {
            // Retrieve patient by id
            Optional<Patient> patientOptional = patientRepository.findById(id);

            if (patientOptional.isPresent()) {
                Patient patient = patientOptional.get();
                PatientView patientView = new PatientView();
                patientView.setId(id);
                patientView.setFirstName(patient.getFirstName());
                patientView.setLastName(patient.getLastName());
                patientView.setBirthdate(patient.getBirthdate());
                patientView.setSsn(patient.getSsn());
                patientView.setStreet(patient.getStreet());
                patientView.setCity(patient.getCity());
                patientView.setState(patient.getState());
                patientView.setZipcode(patient.getZipcode());
                patientView.setPrimaryName(patient.getPrimaryName());
                model.addAttribute("patient", patientView);
                return "patient_edit";
            } else {
                model.addAttribute("message", "Patient not found.");
                return "patient_get";
            }
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            return "patient_get";
        }
    }


    @PostMapping("/patient/edit")
    public String updatePatient(Patient patient, Model model) {
        try {
            // Check if the primary physician name exists
            String primaryPhysicianName = patient.getPrimaryName();
            boolean isPrimaryPhysicianExists = isPrimaryPhysicianExists(primaryPhysicianName);

            if (!isPrimaryPhysicianExists) {
                model.addAttribute("message", "Error: Primary physician name does not exist.");
                return "patient_edit";
            }

            // Update patient data in the database
            patientRepository.save(patient);
            model.addAttribute("message", "Update successful");
            model.addAttribute("patient", patient);
            return "patient_show";
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            return "patient_edit";
        }
    }

    // Method to check if the primary physician exists
    private boolean isPrimaryPhysicianExists(String lastName) {
        Doctor doctor = doctorRepository.findByLastName(lastName);
        return doctor != null; // Return true if doctor is found, false otherwise
    }
}
