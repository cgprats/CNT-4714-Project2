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
public class ProjectTwo {
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
            //Calculate Output
            if (i == 0) {
                outputCalculator = routingStation.length - 1;
            }
            else if (i == routingStation.length -1) {
                outputCalculator = 0;
            }
            else {
                outputCalculator = i - 1;
            }
            //Set the Number of Workloads and Output for Each Routing Station
            routingStation[i] = new RoutingStation(i, configImporter.nextInt(), outputCalculator);
        }
        //Start the Threads
        for (int i = 0; i < routingStation.length; i++) {
            routingStation[i].start();
        }
    }
}
class RoutingStation extends Thread {
    int _id;
    int _workLoadCount;
    int _input;
    int _output;
    //Class Constructor
    RoutingStation(int id, int workLoadCount, int output) {
        _id = id;
        _workLoadCount = workLoadCount;
        _input = id;
        _output = output;
    }
    //Routing Station Thread Actions
    public void run() {
        //Output Initial Process Information
        System.out.println("Routing Station " + _id + ": input connection is set to conveyor number " + _input);
        System.out.println("Routing Station " + _id + ": output connection is set to conveyor number " + _output);
        System.out.println("Routing Station " + _id + ": Workload set. Station " + _id + " has a total of " + _workLoadCount + " package groups to move.");
    }
}