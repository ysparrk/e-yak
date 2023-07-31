package now.eyak.routine.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.NoArgsConstructor;
import now.eyak.prescription.domain.Prescription;

@Entity
@NoArgsConstructor
public class PrescriptionMedicineRoutine {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Prescription prescription;
    @ManyToOne
    private MedicineRoutine medicineRoutine;

    @Builder
    public PrescriptionMedicineRoutine(Long id, Prescription prescription, MedicineRoutine medicineRoutine) {
        this.id = id;
        this.prescription = prescription;
        this.medicineRoutine = medicineRoutine;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public MedicineRoutine getMedicineRoutine() {
        return medicineRoutine;
    }

    public void setMedicineRoutine(MedicineRoutine medicineRoutine) {
        this.medicineRoutine = medicineRoutine;
    }
}
