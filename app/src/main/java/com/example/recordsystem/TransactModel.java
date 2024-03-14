package com.example.recordsystem;

public class TransactModel {

    String pva, petName, medicalHistory, clinicExam, labExam,labExamImage, tentativeDiagnos, finalDiagnos, prognosis, treatment, prescription,nextVisit,clientNote, patientNote,recommendation, currentDate;
    String key;
    public TransactModel() {
    }

    public TransactModel(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public TransactModel(String pva, String petName,
                         String medicalHistory, String clinicExam,
                         String labExam, String labExamImage,
                         String tentativeDiagnos, String finalDiagnos,
                         String prognosis, String treatment,
                         String prescription, String nextVisit,
                         String clientNote, String patientNote,
                         String recommendation, String currentDate) {

        this.pva = pva;
        this.petName = petName;
        this.medicalHistory = medicalHistory;
        this.clinicExam = clinicExam;
        this.labExam = labExam;
        this.labExamImage = labExamImage;
        this.tentativeDiagnos = tentativeDiagnos;
        this.finalDiagnos = finalDiagnos;
        this.prognosis = prognosis;
        this.treatment = treatment;
        this.prescription = prescription;
        this.nextVisit = nextVisit;
        this.clientNote = clientNote;
        this.patientNote = patientNote;
        this.recommendation = recommendation;
        this.currentDate = currentDate;
    }

    public String getPva() {
        return pva;
    }

    public void setPva(String pva) {
        this.pva = pva;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getClinicExam() {
        return clinicExam;
    }

    public void setClinicExam(String clinicExam) {
        this.clinicExam = clinicExam;
    }

    public String getLabExam() {
        return labExam;
    }

    public void setLabExam(String labExam) {
        this.labExam = labExam;
    }

    public String getLabExamImage() {
        return labExamImage;
    }

    public void setLabExamImage(String labExamImage) {
        this.labExamImage = labExamImage;
    }

    public String getTentativeDiagnos() {
        return tentativeDiagnos;
    }

    public void setTentativeDiagnos(String tentativeDiagnos) {
        this.tentativeDiagnos = tentativeDiagnos;
    }

    public String getFinalDiagnos() {
        return finalDiagnos;
    }

    public void setFinalDiagnos(String finalDiagnos) {
        this.finalDiagnos = finalDiagnos;
    }

    public String getPrognosis() {
        return prognosis;
    }

    public void setPrognosis(String prognosis) {
        this.prognosis = prognosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(String nextVisit) {
        this.nextVisit = nextVisit;
    }

    public String getClientNote() {
        return clientNote;
    }

    public void setClientNote(String clientNote) {
        this.clientNote = clientNote;
    }

    public String getPatientNote() {
        return patientNote;
    }

    public void setPatientNote(String patientNote) {
        this.patientNote = patientNote;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
