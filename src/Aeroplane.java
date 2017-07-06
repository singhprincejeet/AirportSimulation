/* The class simulating an aeroplane */
class Aeroplane extends Thread {
    public int          id;
    private Airport    sp;
    private boolean enjoy;
    private int passengersInPlane = 0;
    private Semaphore launchSemaphore;
    private Semaphore passengersOnPlaneSemaphore;
    private Semaphore waitForLandingSemaphore;

    // constructor
    public Aeroplane(Airport sp, int id) {
        this.sp = sp;
        this.id = id;
        enjoy = true;
        waitForLandingSemaphore = new Semaphore(0);
        launchSemaphore = new Semaphore(0);
        passengersOnPlaneSemaphore = new Semaphore(0);
    }

    // the aeroplane thread executes this
    public void run() {
        int     stime;
        int     dest;

        while (enjoy) {
            try {
                // Wait until there an empty landing pad, then land
                dest = sp.wait4landing(this);

                System.out.println("Aeroplane " + id + " landing on pad " + dest);

                // Tell the passengers that we have landed
                if(passengersInPlane == Simulation.PLANE_SIZE){ //check if the plane has passengers
                    for (int i = 0; i < Simulation.PLANE_SIZE; i++) {
                        waitForLandingSemaphore.signalSem();
                        passengersOnPlaneSemaphore.waitSem();
                    }
                }
                System.out.println("Aeroplane " + id + " boarding to "+ Simulation.destName[dest]+" now!");
                // the passengers can start to board now
                sp.boarding(dest);
                for (int i = 0; i < Simulation.PLANE_SIZE; i++) {
                    launchSemaphore.waitSem();
                }

                // 4, 3, 2, 1, Launch!

                System.out.println("Aeroplane " + id + " launches towards "+ Simulation.destName[dest]+"!");

                // tell the passengers we have launched, so they can enjoy now ;-)
                sp.launch(dest);

                // Fly in the air
                stime = 500+(int) (1500*Math.random());
                sleep(stime);
            } catch (InterruptedException e) {
                enjoy = false; // have been interrupted, probably by the main program, terminate
            }
        }
        System.out.println("Aeroplane "+id+" has finished its flights.");
    }


    // service functions to passengers
    // called by the passengers leaving the aeroplane
    public void leave()  throws InterruptedException  {
        passengersInPlane--;
        passengersOnPlaneSemaphore.signalSem();
    }

    // called by the passengers sitting in the aeroplane, to wait
    // until the launch
    public void wait4launch()  throws InterruptedException {
        passengersInPlane++;
        launchSemaphore.signalSem();
    }

    // called by the bored passengers sitting in the aeroplane, to wait
    // until landing
    public void wait4landing()  throws InterruptedException {
        waitForLandingSemaphore.waitSem();
    }
}