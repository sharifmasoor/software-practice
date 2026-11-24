package models;

public class Booking {
    private final String bookingId;
    private final String customerName;
    private final String contact;
    private final String address;
    private final String items;
    private final double total;
    private final String date;
    private final String time;
    private final String instructions;
    private final String status;

    public Booking(String bookingId, String customerName, String contact, String address,
                   String items, double total, String date, String time,
                   String instructions, String status) {
        this.bookingId = bookingId; this.customerName = customerName; this.contact = contact;
        this.address = address; this.items = items; this.total = total; this.date = date;
        this.time = time; this.instructions = instructions; this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public String getCustomerName() { return customerName; }
    public String getContact() { return contact; }
    public String getAddress() { return address; }
    public String getItems() { return items; }
    public double getTotal() { return total; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getInstructions() { return instructions; }
    public String getStatus() { return status; }
}
