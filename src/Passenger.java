/* The class implementing a passenger. */
class Passenger extends Thread {
    private boolean enjoy;
    private int id;
    private Airport sp;

    // constructor
    public Passenger(Airport sp, int id) {
        this.sp = sp;
        this.id = id;
        enjoy = true;
    }

    // this is the passenger's thread
    public void run() {
        int         stime;
        int         dest;
        Aeroplane   sh;

        while (enjoy) {
            try {
                // Wait and arrive to the port
                stime = (int) (700*Math.random());
                sleep(stime);

                // Choose the destination
                dest = (int) (((double) Simulation.DESTINATIONS)*Math.random());
                System.out.println("Passenger " + id + " wants to go to " + Simulation.destName[dest]);

                // come to the airport and board a aeroplane to my destination
                // (might wait if there is no such aeroplane ready)
                sh = sp.wait4Ship(dest);

                // Should be executed after the aeroplane is on the pad and taking passengers
                System.out.println("Passenger " + id + " has boarded aeroplane " + sh.id + ", destination: "+ Simulation.destName[dest]);

                // wait for launch
                sh.wait4launch();

                // Enjoy the ride
                // Should be executed after the aeroplane has launched.
                System.out.println("Passenger "+id+" enjoying the ride to "+ Simulation.destName[dest]+ ": Woohooooo!");

                // wait for landing
                sh.wait4landing();

                // Should be executed after the aeroplane has landed
                System.out.println("Passenger " + id + " leaving the aeroplane " + sh.id);

                // Leave the aeroplane
                sh.leave();
            } catch (InterruptedException e) {
                enjoy = false; // have been interrupted, probably by the main program, terminate
            }
        }
        System.out.println("Passenger "+id+" has finished its rides.");
    }
}