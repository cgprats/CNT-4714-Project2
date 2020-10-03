/*
    Name: Christopher Prats
    Course: CNT 4714 Fall 2020
    Assignment title: Project 2 - Multi-threaded programming in Java
    Date: October 4,2020
    Class: ProjectTwo
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;
public class ProjectTwo {
    //The Public Array of Conveyors. This array is set by the main function and is accessed by each Routing Station.
    public static Conveyor[] conveyors;
    //The Main Function
    public static void main(String[] Args) throws FileNotFoundException {
        //Import the Configuration File
        //The First Line in the File is the Number of Routing Stations
        //The Succeeding Lines is the Amount of Work the Corresponding Station Needs to Process (Line x = Station x - 2)
        File configFile = new File("config.txt");
        Scanner configImporter = new Scanner(configFile);
        //Read the Number of Routing Stations and Create an Array to Contain Each One
        RoutingStation[] routingStation = new RoutingStation[configImporter.nextInt()];
        int outputCalculator;
        for (int i = 0; i < routingStation.length; i++) {
            //Calculate Output Conveyor
            if (i == 0) {
                outputCalculator = routingStation.length - 1;
            }
            else if (i == routingStation.length -1) {
                outputCalculator = 0;
            }
            else {
                outputCalculator = i - 1;
            }
            //Set the Input, Number of Package Groups, and Output for Each Routing Station
            routingStation[i] = new RoutingStation(i, configImporter.nextInt(), outputCalculator);
        }
        //Set the Array of Conveyors
        //Set the size of the conveyor array
        conveyors = new Conveyor[routingStation.length];
        //Set the value of each conveyor in the array
        for (int i = 0; i < conveyors.length; i++) {
            //The conveyor will be identified by the value of i at its time of creation
            conveyors[i] = new Conveyor(i);
        }
        //Start the Threads
        for (int i = 0; i < routingStation.length; i++) {
            routingStation[i].start();
        }
    }
}
//The RoutingStation Class
class RoutingStation extends Thread {
    int _id;
    int _packageGroupCount;
    int _input;
    int _output;
    Random sleepTime = new Random();
    //Class Constructor
    RoutingStation(int id, int packageGroupCount, int output) {
        _id = id;
        _packageGroupCount = packageGroupCount;
        _input = id;
        _output = output;
        //Output Routing Station Information
        System.out.println("Routing Station " + _id + ": input connection is set to conveyor number " + _input);
        System.out.println("Routing Station " + _id + ": output connection is set to conveyor number " + _output);
        System.out.println("Routing Station " + _id + ": Workload set. Station " + _id + " has a total of " + _packageGroupCount + " package groups to move.\n");
    }
    //Routing Station Thread Actions
    public void run() {
        //Begin the Workloads
        boolean packageGroupComplete;
        for (int i = 0; i < _packageGroupCount; i++) {
            //Reset the Package Group Status
            packageGroupComplete = false;
            //Run a Package Group Until it Succeeds
            System.out.println("Routing Station " + _id + ": has " + (_packageGroupCount - i) + " package groups left to move.");
            while (!packageGroupComplete) {
                //Attempt to Lock Input Conveyor
                if (ProjectTwo.conveyors[_input].Lock()) {
                    System.out.println("Routing Station " + _id + ": holds lock on input conveyor " + _input);
                    //Attempt to Unlock Output Conveyor
                    if (ProjectTwo.conveyors[_output].Lock()) {
                        System.out.println("Routing Station " + _id + ": holds lock on output conveyor " + _output);
                        //Move the Package Group if Locks are Held on Both the Input and Output Conveyors
                        packageGroupComplete = doWork();
                        //Unlock Conveyors When Package Group Movement is Complete
                        ProjectTwo.conveyors[_input].Unlock();
                        System.out.println("Routing Station " + _id + ": unlocks input conveyor " + _input);
                        ProjectTwo.conveyors[_output].Unlock();
                        System.out.println("Routing Station " + _id + ": unlocks output conveyor " + _output);
                        //Sleep Thread for Random Amount of Time after the Package Movement Finishes and Threads are Released
                        try {
                            Thread.sleep((long) (sleepTime.nextDouble() * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //If Output Conveyor Can Not be Locked, Unlock Input Conveyor
                    else {
                        ProjectTwo.conveyors[_input].Unlock();
                        System.out.println("Routing Station " + _id + ": unable to lock output conveyor - releasing lock on input conveyor " + _input);
                    }
                }
            }
        }
        System.out.println("* * Station " + _id + ": Workgroup successfully completed. * *");
    }
    public boolean doWork() {
        //Check if the Current Routing Station Holds Locks on Both the Input and Output Conveyors
        if (ProjectTwo.conveyors[_input].OwnedByCurrent() && ProjectTwo.conveyors[_output].OwnedByCurrent()) {
            //If a Lock on Both Conveyors is Held, Move the Conveyors and Return True (Successful)
            System.out.println("Routing Station " + _id + ": successfully moves packages into station on input conveyor " + _input);
            System.out.println("Routing Station " + _id + ": successfully moves packages out of station on output conveyor " + _output);
            return true;
        }
        else {
            //If a Lock is not Held on Both Conveyors, Return False (Failure)
            return false;
        }
    }
}
//The Conveyor Class
class Conveyor {
    int _conveyorId;
    ReentrantLock _lockStatus = new ReentrantLock();
    //Class Constructor
    public Conveyor(int conveyorID) {
        _conveyorId = conveyorID;
    }
    //Lock the Conveyor
    public boolean Lock() {
        //Attempts to Grab Lock if Available. If It's Not Available, returns false
        return _lockStatus.tryLock();
    }
    //Unlock the Conveyor
    public void Unlock() {
        //Unlocks the Thread
        _lockStatus.unlock();
    }
    public boolean OwnedByCurrent() {
        //Return the Ownership of the Accessing Thread on the Lock
        return _lockStatus.isHeldByCurrentThread();
    }
}