//package application;
//
//
//import application.model.*;
//import application.service.SequenceService;
//import org.bson.codecs.jsr310.LocalDateCodec;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import view.PrescriptionView;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Optional;
//
//@Controller
//public class ControllerPrescriptionFill {
//    @Autowired
//    private DoctorRepository doctorRepository;
//
//    @Autowired
//    private PatientRepository patientRepository;
//
//    @Autowired
//    private DrugRepository drugRepository;
//
//
//    @Autowired
//    private PrescriptionRepository prescriptionRepository;
//
//    @Autowired
//    private PharmacyRepository pharmacyRepository;
//
//
//    @GetMapping("/prescription/fill")
//    public String getPrescriptionFillForm(Model model) {
//        model.addAttribute("prescription", new PrescriptionView());
//        return "prescription_fill";
//    }
//
//    public String fillPrescription(PrescriptionView p, Model model) {
//        /*
//         * valid pharmacy name and address, get pharmacy id and phone
//         */
//        if (pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress()) == null) {
//            model.addAttribute("message", "Pharmacy not found");
//            model.addAttribute("prescription", p);
//            return "prescription_fill";
//        }
//        ;
//
//        //Insert the prescription to a collection
//        //Check if rx exists and then determine whether to fill the rx or not
//        Optional<Prescription> optionalPrescription = prescriptionRepository.findById(p.getRxid());
//        if (optionalPrescription.isPresent()) {
//            Prescription prescription = optionalPrescription.get();
//            //Get # of refills already done and determine whether to fill an rx
//            if (prescription.getFills().size() > prescription.getRefills()) {
//                model.addAttribute("message", "This prescription cannot be refilled.");
//                model.addAttribute("prescription", p);
//                return "prescription_fill";
//            } else {
//                //Since there are fills insert the prescription to a collection
//
//
//                //add presciption fill to prescription.getFills(), it's an arrayList
//                //of FillRequest
//
//                //Prescription fill is an inner class of Prescription so the method
//                //has to be written while referencing the inner class
//
//                //Set view values that are not Prescription
//
//                //Start with setting dr
//                int drID = prescription.getDoctorId();
//                p.setDoctorId(drID);
//                Doctor dr = doctorRepository.findById(drID);
//                p.setDoctorFirstName(dr.getFirstName());
//                p.setDoctorLastName(dr.getLastName());
//
//
//                //set patient
//                int ptID = prescription.getPatientId();
//                Optional<Patient> optionalPatient = patientRepository.findById(ptID);
//                Patient pt = null;
//                if (optionalPatient.isPresent()) {
//                    pt = optionalPatient.get();
//                }
//                if (pt != null) {
//                    p.setPatientFirstName(pt.getFirstName());
//                    p.setPatientLastName(pt.getLastName());
//                }
//
//                //set
//
//
//                Prescription.FillRequest prescriptionFill = new Prescription.FillRequest();
//
//                prescriptionFill.setPharmacyID(p.getPharmacyID());
//
//                //set date filled
//                LocalDateCodec date = new LocalDateCodec();
//                prescriptionFill.setDateFilled(String.valueOf(date));
//                p.setDateFilled(String.valueOf(date));
//
//                //Get drug quantity, drug name, and add them to prescription view
//                //add calculate prescription cost and add it to view
//
//
//                //Get drug and set drug's name in view
//                Drug drug = drugRepository.findByName(p.getDrugName());
//                p.setDrugName(drug.getName());
//                //Drugcost x quantity
//                Pharmacy.DrugCost drugCost = pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress()).getDrugCosts().get(drug.getId());
//                double prescriptionCost = prescription.getQuantity() * drugCost.getCost();
//                p.setQuantity(prescription.getQuantity());
//                prescriptionFill.setCost(String.valueOf(prescriptionCost));
//                p.setCost(String.valueOf(prescriptionCost));
//
//                //Add current rx fill to the fillRequests arrayList
//                //Update fillRequests and put number of refills remaining in the view
//                ArrayList<Prescription.FillRequest> fillRequests = prescription.getFills();
//                fillRequests.add(prescriptionFill);
//                p.setRefillsRemaining(prescription.getRefills() - prescription.getFills().size());
//
//                model.addAttribute("message", "Prescription filled.");
//                model.addAttribute("prescription", p);
//                return "prescription_show";
//
//
//            }
//        } else {
//            //State that rx is not present bc rxid is not found
//            model.addAttribute("message", "Rxid not found.");
//            model.addAttribute("prescription", p);
//            return "prescription_fill";
//        }
//    }
//}
