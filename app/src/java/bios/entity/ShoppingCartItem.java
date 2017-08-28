package bios.entity;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
public class ShoppingCartItem {
    
    private Student student;
    private Section section;
    private double amount;
    
    /**
     * Specific Constructor
     * @param student Takes in student object of Student type
     * @param section Takes in section object of Section type
     */
    public ShoppingCartItem(Student student, Section section) {
        this.student = student;
        this.section = section;
        this.amount = 0.0;
    }
    
    /**
     * Set the ShoppingCartItem Amount
     * @param amount - the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Get the ShoppingCartItem Section
     * @return the current Section object
     */
    public Section getSection() {
        return section;
    }

    /**
     * Get the ShoppingCartItem Amount
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * Get the ShoppingCartItem Student 
     * @return the Student object
     */
    public Student getStudent() {
        return student;
    }
    
}
