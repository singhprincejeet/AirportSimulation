/* The class implementing the Airport. */
class Airport {
    Aeroplane[]    pads; // what is sitting on a given pad

    private Semaphore landingPadsSemaphore = new Semaphore(Simulation.LANDING_PADS);
    private Semaphore[] aeroplaneBoardingSemaphore = {new Semaphore(0), new Semaphore(0),
            new Semaphore(0), new Semaphore(0)};
    // constructor
    public Airport() {
        int i;

        pads = new Aeroplane[Simulation.DESTINATIONS];

        // pads[] is an array containing the aeroplanes sitting on corresponding pads
        // Value null means the pad is empty
        for(i=0; i< Simulation.DESTINATIONS; i++) {
            pads[i] = null;
        }

    }

    // called by a passenger wanting to go to the given destination
    // returns the aeroplane he/she boarded
    // Careful here, as the pad might be empty at this moment
    public Aeroplane wait4Ship(int dest) throws InterruptedException {
        aeroplaneBoardingSemaphore[dest].waitSem();
        return pads[dest];
    }

    // called by an aeroplane to tell the airport that it is accepting passengers now to destination dest
    public void boarding(int dest) {
        for (int i = 0; i < Simulation.PLANE_SIZE; i++) {
            aeroplaneBoardingSemaphore[dest].signalSem();
        }
    }

    // Called by an aeroplane returning from a trip
    // Returns the number of the empty pad where to land (might wait
    // until there is an empty pad).
    // Try to rotate the pads so that no destination is starved
    public int wait4landing(Aeroplane sh)  throws InterruptedException  {
        landingPadsSemaphore.waitSem();
        int dest = (int)((Simulation.DESTINATIONS)*Math.random());
        while (pads[dest] != null){
            dest = (dest+1)% Simulation.DESTINATIONS;
        }
        pads[dest] = sh;
        return dest;
    }

    // called by an aeroplane when it launches, to inform the
    // airport that the pad has been emptied
    public void launch(int dest) {
        pads[dest] = null;
        landingPadsSemaphore.signalSem();
    }
}