// Skeleton Program code for the AQA A Level Paper 1 Summer 2026 examination
// this code should be used in conjunction with the Preliminary Material
// written by the AQA Programmer Team
// developed in the IntelliJ IDEA Community Edition programming environment
// Version 2

package simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class AntSimulation {
    static Scanner scanner = new Scanner(System.in);

    static Random rGen = new Random();

    static class IntWrapper {
        public int value;

        IntWrapper(int initialValue) {
            value = initialValue;
        }
    }

    public static void main(String[] args) throws Exception {
        ArrayList<Integer> simulationParameters = new ArrayList<Integer>();
        System.out.print("Enter simulation number: ");
        String simNo = scanner.nextLine();
        switch (simNo) {
            case "1":
                simulationParameters = new ArrayList<Integer>(Arrays.asList(1, 5, 5, 500, 3, 5, 1000, 50));
                break;
            case "2":
                simulationParameters = new ArrayList<Integer>(Arrays.asList(1, 5, 5, 500, 3, 5, 1000, 100));
                break;
            case "3":
                simulationParameters = new ArrayList<Integer>(Arrays.asList(1, 10, 10, 500, 3, 9, 1000, 25));
                break;
            case "4":
                simulationParameters = new ArrayList<Integer>(Arrays.asList(2, 10, 10, 500, 3, 6, 1000, 25));
                break;
        }
        Simulation thisSimulation = new Simulation(simulationParameters);
        String choice;
        while(true){
            displayMenu();
            choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        System.out.println(thisSimulation.getDetails());
                        break;
                    case "2":
                        IntWrapper startRow = new IntWrapper(0), startColumn = new IntWrapper(0);
                        IntWrapper endRow = new IntWrapper(0), endColumn = new IntWrapper(0);
                        getCellReference(startRow, startColumn);
                        getCellReference(endRow, endColumn);
                        System.out.println(thisSimulation.getAreaDetails(startRow.value, startColumn.value, endRow.value, endColumn.value));
                        break;
                    case "3":
                        IntWrapper row = new IntWrapper(0), column = new IntWrapper(0);
                        getCellReference(row, column);
                        System.out.println(thisSimulation.getCellDetails(row.value, column.value));
                        break;
                    case "4":
                        thisSimulation.advanceStage(1);
                        System.out.println("Simulation moved on one stage\n");
                        break;
                    case "5":
                        System.out.print("Enter number of stages to advance by: ");
                        int numberOfStages = Integer.parseInt(scanner.nextLine());
                        thisSimulation.advanceStage(numberOfStages);
                        System.out.println("Simulation moved on " + numberOfStages + " stages\n");
                        break;
                    case "9":
                        System.out.println("Are you sure you want to quit : yes/no");
                        choice = scanner.nextLine();
                        if(choice.equals("yes")){
                            System.exit(0);
                        }
                        break;

                }
            }

    }

    static void displayMenu() {
        System.out.println();
        System.out.println("1. Display overall details");
        System.out.println("2. Display area details");
        System.out.println("3. Inspect cell");
        System.out.println("4. Advance one stage");
        System.out.println("5. Advance X stages");
        System.out.println("9. Quit");
        System.out.println();
        System.out.print("> ");
    }

    static String getChoice() {
        String choice = scanner.nextLine();
        return choice;
    }

    static void getCellReference(IntWrapper row, IntWrapper column) {
        System.out.println();
        System.out.print("Enter row number: ");
        row.value = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter column number: ");
        column.value = Integer.parseInt(scanner.nextLine());
        System.out.println();
    }

    static class Simulation {
        protected ArrayList<Cell> grid = new ArrayList<Cell>();
        protected ArrayList<Ant> ants = new ArrayList<Ant>();
        protected ArrayList<Pheromone> pheromones = new ArrayList<Pheromone>();
        protected ArrayList<Nest> nests = new ArrayList<Nest>();
        protected int numberOfRows, numberOfColumns, startingFoodInNest, startingNumberOfFoodCells,
                startingNumberOfNests;
        protected int startingAntsInNest, newPheromoneStrength, pheromoneDecay;

        Simulation(ArrayList<Integer> simulationParameters) {
            startingNumberOfNests = simulationParameters.get(0);
            numberOfRows = simulationParameters.get(1);
            numberOfColumns = simulationParameters.get(2);
            startingFoodInNest = simulationParameters.get(3);
            startingNumberOfFoodCells = simulationParameters.get(4);
            startingAntsInNest = simulationParameters.get(5);
            newPheromoneStrength = simulationParameters.get(6);
            pheromoneDecay = simulationParameters.get(7);
            int row, column;
            for (row = 1; row <= numberOfRows; row++) {
                for (column = 1; column <= numberOfColumns; column++) {
                    grid.add(new Cell(row, column));
                }
            }
            setUpANestAt(2, 4);
            for (int count = 2; count <= startingNumberOfNests; count++) {
                boolean allowed;
                do {
                    allowed = true;
                    row = rGen.nextInt(numberOfRows) + 1;
                    column = rGen.nextInt(numberOfColumns) + 1;
                    for (Nest n : nests)
                        if (n.getRow() == row && n.getColumn() == column) {
                            allowed = false;
                        }
                } while (!allowed);
                setUpANestAt(row, column);
            }
            for (int count = 1; count <= startingNumberOfFoodCells; count++) {
                boolean allowed;
                do {
                    allowed = true;
                    row = rGen.nextInt(numberOfRows) + 1;
                    column = rGen.nextInt(numberOfColumns) + 1;
                    for (Nest n : nests) {
                        if (n.getRow() == row && n.getColumn() == column) {
                            allowed = false;
                        }
                    }
                } while (!allowed);
                addFoodToCell(row, column, 500);
            }
        }

        public void setUpANestAt(int row, int column) {
            nests.add(new Nest(row, column, startingFoodInNest));
            ants.add(new QueenAnt(row, column, row, column));
            for (int worker = 2; worker <= startingAntsInNest; worker++) {
                ants.add(new WorkerAnt(row, column, row, column));
            }
        }

        public void addFoodToCell(int row, int column, int quantity) {
            grid.get(getIndex(row, column)).updateFoodInCell(quantity);
        }

        private int getIndex(int row, int column) {
            return (row - 1) * numberOfColumns + column - 1;
        }

        private ArrayList<Integer> getIndicesOfNeighbours(int row, int column) {
            ArrayList<Integer> listOfNeighbours = new ArrayList<Integer>();
            for (Integer rowDirection : Arrays.asList(-1, 0, 1)) {
                for (Integer columnDirection : Arrays.asList(-1, 0, 1)) {
                    int neighbourRow = row + rowDirection, neighbourColumn = column + columnDirection;
                    if ((rowDirection != 0 || columnDirection != 0) &&
                            (neighbourRow >= 1) &&
                            (neighbourRow <= numberOfRows) &&
                            (neighbourColumn >= 1) &&
                            (neighbourColumn <= numberOfColumns)) {
                        listOfNeighbours.add(getIndex(neighbourRow, neighbourColumn));
                    } else {
                        listOfNeighbours.add(-1);
                    }
                }
            }
            return listOfNeighbours;
        }

        private int getIndexOfNeighbourWithStrongestPheromone(int row, int column) {
            int strongestPheromone = 0, indexOfStrongestPheromone = -1;
            for (Integer index : getIndicesOfNeighbours(row, column)) {
                if (index != -1 && getStrongestPheromoneInCell(grid.get(index)) > strongestPheromone) {
                    indexOfStrongestPheromone = index;
                    strongestPheromone = getStrongestPheromoneInCell(grid.get(index));
                }
            }
            return indexOfStrongestPheromone;
        }

        public Nest getNestInCell(Cell c) {
            for (Nest n : nests) {
                if (n.inSameLocation(c)) {
                    return n;
                }
            }
            return null;
        }

        public void updateAntsPheromoneInCell(Ant a) {
            for (Pheromone p : pheromones) {
                if (p.inSameLocation(a) && p.getBelongsTo() == a.getID()) {
                    p.updateStrength(newPheromoneStrength);
                    return;
                }
            }
            pheromones.add(new Pheromone(a.getRow(), a.getColumn(), a.getID(), newPheromoneStrength, pheromoneDecay));
        }

        public int getNumberOfAntsInCell(Cell c) {
            int count = 0;
            for (Ant a : ants) {
                if (a.inSameLocation(c)) {
                    count++;
                }
            }
            return count;
        }

        public int getNumberOfPheromonesInCell(Cell c) {
            int count = 0;
            for (Pheromone p : pheromones) {
                if (p.inSameLocation(c)) {
                    count++;
                }
            }
            return count;
        }

        public int getStrongestPheromoneInCell(Cell c) {
            int strongest = 0;
            for (Pheromone p : pheromones) {
                if (p.inSameLocation(c)) {
                    if (p.getStrength() > strongest) {
                        strongest = p.getStrength();
                    }
                }
            }
            return strongest;
        }

        public String getDetails() {
            String details = "";
            for (int row = 1; row <= numberOfRows; row++) {
                for (int column = 1; column <= numberOfColumns; column++) {
                    details += row + ", " + column + ": ";
                    Cell tempCell = grid.get(getIndex(row, column));
                    if (getNestInCell(tempCell) != null) {
                        details += "| Nest |  ";
                    }
                    int numberOfAnts = getNumberOfAntsInCell(tempCell);
                    if (numberOfAnts > 0) {
                        details += "| Ants: " + numberOfAnts + " |  ";
                    }
                    int numberOfPheromones = getNumberOfPheromonesInCell(tempCell);
                    if (numberOfPheromones > 0) {
                        details += "| Pheromones: " + numberOfPheromones + " |  ";
                    }
                    int amountOfFood = tempCell.getAmountOfFood();
                    if (amountOfFood > 0) {
                        details += "| " + amountOfFood + " food |  ";
                    }
                    details += "\n";
                }
            }
            return details;
        }

        public String getAreaDetails(int startRow, int startColumn, int endRow, int endColumn) {
            String details = "";
            for (int row = startRow; row <= endRow; row++) {
                for (int column = startColumn; column <= endColumn; column++) {
                    details += row + ", " + column + ": ";
                    Cell tempCell = grid.get(getIndex(row, column));
                    if (getNestInCell(tempCell) != null) {
                        details += "| Nest |  ";
                    }
                    int numberOfAnts = getNumberOfAntsInCell(tempCell);
                    if (numberOfAnts > 0) {
                        details += "| Ants: " + numberOfAnts + " |  ";
                    }
                    int numberOfPheromones = getNumberOfPheromonesInCell(tempCell);
                    if (numberOfPheromones > 0) {
                        details += "| Pheromones: " + numberOfPheromones + " |  ";
                    }
                    int amountOfFood = tempCell.getAmountOfFood();
                    if (amountOfFood > 0) {
                        details += "| " + amountOfFood + " food |  ";
                    }
                    details += "\n";
                }
            }
            return details;
        }

        public void addFoodToNest(int food, int row, int column) {
            for (Nest n : nests) {
                if (n.getRow() == row && n.getColumn() == column) {
                    n.changeFood(food);
                    break;
                }
            }
        }

        public String getCellDetails(int row, int column) {
            Cell currentCell = grid.get(getIndex(row, column));
            String details = currentCell.getDetails();
            Nest n = getNestInCell(currentCell);
            if (n != null) {
                details += "Nest present (" + n.getFoodLevel() + " food)\n\n";
            }
            if (getNumberOfAntsInCell(currentCell) > 0) {
                details += "ANTS\n";
                for (Ant a : ants) {
                    if (a.inSameLocation(currentCell)) {
                        details += a.getDetails() + "\n";
                    }
                }
                details += "\n\n";
            }
            if (getNumberOfPheromonesInCell(currentCell) > 0) {
                details += "PHEROMONES\n";
                for (Pheromone p : pheromones) {
                    if (p.inSameLocation(currentCell)) {
                        details += "Ant " + p.getBelongsTo() + " with strength of " + p.getStrength() + "\n\n";
                    }
                }
                details += "\n\n";
            }
            return details;
        }

        public void advanceStage(int numberOfStages) {
            for (int count = 1; count <= numberOfStages; count++) {
                ArrayList<Pheromone> pheromonesToDelete = new ArrayList<Pheromone>();
                for (Pheromone p : pheromones) {
                    p.advanceStage(nests, ants, pheromones);
                    if (p.getStrength() == 0) {
                        pheromonesToDelete.add(p);
                    }
                }
                for (Pheromone p : pheromonesToDelete) {
                    pheromones.remove(p);
                }
                for (Ant a : ants) {
                    a.advanceStage(nests, ants, pheromones);
                    Cell currentCell = grid.get(getIndex(a.getRow(), a.getColumn()));
                    if ((a.getFoodCarried() > 0) && a.isAtOwnNest()) {
                        addFoodToNest(a.getFoodCarried(), a.getRow(), a.getColumn());
                        a.updateFoodCarried(-a.getFoodCarried());
                    } else if ((currentCell.getAmountOfFood() > 0) && (a.getFoodCarried() == 0) && (a.getFoodCapacity() > 0)) {
                        int foodObtained;
                        do {
                            foodObtained = rGen.nextInt(a.getFoodCapacity()) + 1;
                        } while (!(foodObtained <= currentCell.getAmountOfFood()
                                && (a.getFoodCarried() + foodObtained) <= a.getFoodCapacity()));
                        currentCell.updateFoodInCell(-foodObtained);
                        a.updateFoodCarried(foodObtained);
                    } else {
                        if (a.getFoodCarried() > 0) {
                            updateAntsPheromoneInCell(a);
                        }
                        a.chooseCellToMoveTo(getIndicesOfNeighbours(a.getRow(), a.getColumn()), getIndexOfNeighbourWithStrongestPheromone(a.getRow(), a.getColumn()));
                    }
                    if(a.getStages() >= 14){
                        ants.remove(a);
                    }
                }
                for (Nest n : nests) {
                    n.advanceStage(nests, ants, pheromones);
                }
            }
        }
    }

    static class Entity {
        protected int row, column, id;

        Entity(int startRow, int startColumn) {
            row = startRow;
            column = startColumn;
        }

        public boolean inSameLocation(Entity e) {
            return e.getRow() == row && e.getColumn() == column;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public int getID() {
            return id;
        }

        public void advanceStage(ArrayList<Nest> nests, ArrayList<Ant> ants, ArrayList<Pheromone> pheromones) {
        }

        public String getDetails() {
            return "";
        }
    }

    static class Cell extends Entity {
        protected int amountOfFood;

        Cell(int startRow, int startColumn) {
            super(startRow, startColumn);
            amountOfFood = 0;
        }

        public int getAmountOfFood() {
            return amountOfFood;
        }

        @Override
        public String getDetails() {
            String details = super.getDetails() + amountOfFood + " food present\n\n";
            return details;
        }

        public void updateFoodInCell(int change) {
            amountOfFood += change;
        }
    }

    static class Ant extends Entity {
        protected int nestRow, nestColumn, amountOfFoodCarried, stages, foodCapacity;
        protected String typeOfAnt;

        static int nextAntID = 1;

        Ant(int startRow, int startColumn, int nestInRow, int nestInColumn) {
            super(startRow, startColumn);
            nestRow = nestInRow;
            nestColumn = nestInColumn;
            id = nextAntID;
            nextAntID += 1;
            stages = 0;
            amountOfFoodCarried = 0;
            foodCapacity = 0;
            typeOfAnt = "";
        }

        public int getFoodCapacity() {
            return foodCapacity;
        }

        public boolean isAtOwnNest() {
            return row == nestRow && column == nestColumn;
        }

        public int getStages() {
            return stages;
        }

        @Override
        public void advanceStage(ArrayList<Nest> nests, ArrayList<Ant> ants, ArrayList<Pheromone> pheromones) {
            stages += 1;
        }

        @Override
        public String getDetails() {
            return super.getDetails() + "  Ant " + id + ", " + typeOfAnt + ", stages alive: " + stages;
        }

        public void updateFoodCarried(int change) {
            amountOfFoodCarried += change;
        }

        protected void changeCell(int newCellIndicator, IntWrapper rowToChange, IntWrapper columnToChange) {
            if (newCellIndicator > 5) {
                rowToChange.value += 1;
            } else if (newCellIndicator < 3) {
                rowToChange.value -= 1;
            }
            if (Arrays.asList(0, 3, 6).contains(newCellIndicator)) {
                columnToChange.value -= 1;
            } else if (Arrays.asList(2, 5, 8).contains(newCellIndicator)) {
                columnToChange.value += 1;
            }
        }

        protected int chooseRandomNeighbour(ArrayList<Integer> listOfNeighbours) {
            int rNo = 0;
            do {
                rNo = rGen.nextInt(listOfNeighbours.size());
            } while (listOfNeighbours.get(rNo) == -1);
            return rNo;
        }

        public void chooseCellToMoveTo(ArrayList<Integer> listOfNeighbours, int indexOfNeighbourWithStrongestPheromone) {
        }

        public int getFoodCarried() {
            return amountOfFoodCarried;
        }

        public int getNestRow() {
            return nestRow;
        }

        public int getNestColumn() {
            return nestColumn;
        }

        public String getTypeOfAnt() {
            return typeOfAnt;
        }
    }

    static class QueenAnt extends Ant {
        QueenAnt(int startRow, int startColumn, int nestInRow, int nestInColumn) {
            super(startRow, startColumn, nestInRow, nestInColumn);
            typeOfAnt = "queen";
        }
    }

    static class WorkerAnt extends Ant {
        WorkerAnt(int startRow, int startColumn, int nestInRow, int nestInColumn) {
            super(startRow, startColumn, nestInRow, nestInColumn);
            typeOfAnt = "worker";
            foodCapacity = 30;
        }

        @Override
        public String getDetails() {
            return super.getDetails() + ", carrying " + amountOfFoodCarried + " food, home nest is at " + nestRow + " "
                    + nestColumn;
        }

        @Override
        public void chooseCellToMoveTo(ArrayList<Integer> listOfNeighbours, int indexOfNeighbourWithStrongestPheromone) {
            IntWrapper r, c;
            if (amountOfFoodCarried > 0) {
                if (row > nestRow) {
                    row -= 1;
                } else if (row < nestRow) {
                    row += 1;
                }
                if (column > nestColumn) {
                    column -= 1;
                } else if (column < nestColumn) {
                    column += 1;
                }
            } else if (indexOfNeighbourWithStrongestPheromone == -1) {
                int indexToUse = chooseRandomNeighbour(listOfNeighbours);
                r = new IntWrapper(row);
                c = new IntWrapper(column);
                changeCell(indexToUse, r, c);
                row = r.value;
                column = c.value;
            } else {
                int indexToUse = listOfNeighbours.indexOf(indexOfNeighbourWithStrongestPheromone);
                r = new IntWrapper(row);
                c = new IntWrapper(column);
                changeCell(indexToUse, r, c);
                row = r.value;
                column = c.value;
            }
        }
    }

    static class Nest extends Entity {
        protected int foodLevel, numberOfQueens;

        static int nextNestID = 1;

        Nest(int startRow, int startColumn, int startFood) {
            super(startRow, startColumn);
            foodLevel = startFood;
            numberOfQueens = 1;
            id = nextNestID;
            nextNestID += 1;
        }

        public void changeFood(int change) {
            foodLevel += change;
            if (foodLevel < 0) {
                foodLevel = 0;
            }
        }

        public int getFoodLevel() {
            return foodLevel;
        }

        @Override
        public void advanceStage(ArrayList<Nest> nests, ArrayList<Ant> ants, ArrayList<Pheromone> pheromones) {
            if (ants == null) {
                return;
            }
            int antsToCull = 0;
            int count = 0;
            int antsInNestCount = 0;
            for (Ant a : ants) {
                if (a.getNestRow() == row && a.getNestColumn() == column) {
                    if (a.getTypeOfAnt().equals("queen")) {
                        count += 10;
                    } else {
                        count += 2;
                        antsInNestCount += 1;
                    }
                }
            }
            changeFood(-count);
            if (foodLevel == 0 && antsInNestCount > 0) {
                antsToCull += 1;
            }
            if (foodLevel < antsInNestCount) {
                antsToCull += 1;
            }
            if (foodLevel < antsInNestCount * 5) {
                antsToCull += 1;
                if (antsToCull > antsInNestCount) {
                    antsToCull = antsInNestCount;
                }
                for (int a = 1; a <= antsToCull; a++) {
                    int rPos;
                    do {
                        rPos = rGen.nextInt(ants.size());
                    } while (!(ants.get(rPos).getNestRow() == row && ants.get(rPos).getNestColumn() == column));
                    if (ants.get(rPos).getTypeOfAnt().equals("queen")) {
                        numberOfQueens -= 1;
                    }
                    ants.remove(rPos);
                }
            } else {
                for (int a = 1; a <= numberOfQueens; a++) {
                    int rNo1 = rGen.nextInt(100);
                    if (rNo1 < 50) {
                        int rNo2 = rGen.nextInt(100);
                        if (rNo2 < 2) {
                            ants.add(new QueenAnt(row, column, row, column));
                            numberOfQueens += 1;
                        } else {
                            ants.add(new WorkerAnt(row, column, row, column));
                        }
                    }
                }
            }
        }
    }

    static class Pheromone extends Entity {
        protected int strength, pheromoneDecay, belongsTo;

        Pheromone(int row, int column, int belongsToAnt, int initialStrength, int decay) {
            super(row, column);
            belongsTo = belongsToAnt;
            strength = initialStrength;
            pheromoneDecay = decay;
        }

        @Override
        public void advanceStage(ArrayList<Nest> nests, ArrayList<Ant> ants, ArrayList<Pheromone> pheromones) {
            strength -= pheromoneDecay;
            if (strength < 0) {
                strength = 0;
            }
        }

        public void updateStrength(int change) {
            strength += change;
        }

        public int getStrength() {
            return strength;
        }

        public int getBelongsTo() {
            return belongsTo;
        }
    }
}