package bios.servlet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Admin
 */
import bios.entity.Student;
import bios.entity.Bid;
import bios.entity.ShoppingCart;
import bios.entity.ShoppingCartItem;
import bios.dao.BidDAO;
import java.util.*;

/**
 *
 * @author qyk1994
 */
public class BidController {

    private BidDAO bidDAO;
    private ShoppingCart shoppingCart;

    /**
     * Creates a Bidcontroller objective with the BidDAO object and ShoppingCart
     * Object passed in
     *
     * @param bidDAO - BidDAO object that is passed in
     * @param shoppingCart - shoppingCart object that is passed in
     */
    public BidController(BidDAO bidDAO, ShoppingCart shoppingCart) {
        this.bidDAO = bidDAO;
        this.shoppingCart = shoppingCart;
    }

    //check out method
    /**
     * Retrieve all the shoppingCartItems from the shopping cart Create bids for
     * each shoppingCartItem within the shopping cart
     *
     * @param student - student object that is passed in
     * @param bidAmount - bidAmount that is passed in
     * @param courseID - CourseID that is passed in
     * @param sectionID - sectionID that is passed in
     */
    public void addBids(Student student, double bidAmount, String courseID, String sectionID) {
        //first, retrieve all shoppingcartitems
        String[] roundStatus = bidDAO.getCurrentBidRound();
        int currentRound = Integer.parseInt(roundStatus[1]);
        ArrayList<ShoppingCartItem> shoppingCartItems = shoppingCart.getShoppingCartItems();
        //create bids for the shoppingcartitems
        for (ShoppingCartItem s : shoppingCartItems) {
            Bid bid = new Bid(s.getStudent(), s.getAmount(), s.getSection().getCourseID(), s.getSection().getSectionID(), "Pending", currentRound);
            bidDAO.addBid(bid);
        }
    }
}
