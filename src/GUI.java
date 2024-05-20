import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.awt.Graphics;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame {
	
    private static final long serialVersionUID = 1L;

    public int mouseX = 0;
	public int mouseY = 0;

	private int cols;
	private int rows;
	private static int counter;
	private int numMines;
	private int totalFlags;

	private ArrayList<Integer> invalid;
	
	public static int spacing = 2; //Space between boxes
	private static int originX = 50; //starting X position for drawing mine field
	private static int originY = 100; //starting Y position for drawing mine field
	private static int sizeX = 1216;
	private static int sizeY = 835;
	private int tileSize;
	
	private int difficulty = 1; //0 = easy, 1 = normal, 2 = hard
	private int boardSize = 1; //0 = small, 1 = medium, 2 = large
	
	private boolean firstMove = false;
	private boolean gameOver = false;
	private boolean mainMenu = true;
	private boolean playMenu = false;
	private boolean statsMenu = false;

	private int[][] mines;
	private int[][] mineProx;
	private boolean[][] flagged;
	private boolean[][] revealed;
	
	private int[] stats = new int[5];
	
	public GUI() 
	{
        this.setTitle("Minesweeper");
        this.setSize(sizeX, sizeY);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        Board board = new Board();
        this.setContentPane(board);
        
        Move move = new Move();
        this.addMouseMotionListener(move);
        
        Click click = new Click();
        this.addMouseListener(click);
    }
	
	private void mineProx()
	{
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (mines[i][j] == 1)
				{
					counter = 9;
				}
				else if (i == 0 && j == 0)
				{
					if (mines[i][j + 1] == 1) {counter++;}
					if (mines[i + 1][j + 1] == 1) {counter++;}
					if (mines[i + 1][j] == 1) {counter++;}
				}
				else if (i == 0 && j == cols - 1)
				{
					if (mines[i][j - 1] == 1) {counter++;}
					if (mines[i + 1][j - 1] == 1) {counter++;}
					if (mines[i + 1][j] == 1) {counter++;}
				}
				else if (i == rows - 1 && j == 0)
				{
					if (mines[i][j + 1] == 1) {counter++;}
					if (mines[i - 1][j + 1] == 1) {counter++;}
					if (mines[i - 1][j] == 1) {counter++;}
				}
				else if (i == rows - 1 && j == cols - 1)
				{
					if (mines[i][j - 1] == 1) {counter++;}
					if (mines[i - 1][j - 1] == 1) {counter++;}
					if (mines[i - 1][j] == 1) {counter++;}
				}
				else if (i == 0)
				{
					if (mines[i][j - 1] == 1) {counter++;}
					if (mines[i][j + 1] == 1) {counter++;}
					if (mines[i + 1][j - 1] == 1) {counter++;}
					if (mines[i + 1][j] == 1) {counter++;}
					if (mines[i + 1][j + 1] == 1) {counter++;}
				}
				else if (i == rows - 1)
				{
					if (mines[i][j - 1] == 1) {counter++;}
					if (mines[i][j + 1] == 1) {counter++;}
					if (mines[i - 1][j - 1] == 1) {counter++;}
					if (mines[i - 1][j] == 1) {counter++;}
					if (mines[i - 1][j + 1] == 1) {counter++;}
				}
				else if (j == 0)
				{
					if (mines[i - 1][j] == 1) {counter++;}
					if (mines[i + 1][j] == 1) {counter++;}
					if (mines[i - 1][j + 1] == 1) {counter++;}
					if (mines[i][j + 1] == 1) {counter++;}
					if (mines[i + 1][j + 1] == 1) {counter++;}
				}
				else if (j == cols - 1)
				{
					if (mines[i - 1][j] == 1) {counter++;}
					if (mines[i + 1][j] == 1) {counter++;}
					if (mines[i - 1][j - 1] == 1) {counter++;}
					if (mines[i][j - 1] == 1) {counter++;}
					if (mines[i + 1][j - 1] == 1) {counter++;}
				}
				else 
				{
					if (mines[i - 1][j - 1] == 1) {counter++;}
					if (mines[i - 1][j] == 1) {counter++;}
					if (mines[i - 1][j + 1] == 1) {counter++;}
					if (mines[i][j - 1] == 1) {counter++;}
					if (mines[i][j + 1] == 1) {counter++;}
					if (mines[i + 1][j - 1] == 1) {counter++;}
					if (mines[i + 1][j] == 1) {counter++;}
					if (mines[i + 1][j + 1] == 1) {counter++;}
				}
				mineProx[i][j] = counter;
				counter = 0;
			}
		}
	}
	
	private void fillMines(int firstRow, int firstCol)
	{
		int num = ((firstRow * cols) + firstCol);
				
		invalid.add(num);
		invalid.add(num - 1);
		invalid.add(num + 1);
		invalid.add(num - 1 + cols);
		invalid.add(num - 1 - cols);
		invalid.add(num + 1 + cols);
		invalid.add(num + 1 - cols);
		invalid.add(num + cols);
		invalid.add(num - cols);
		
		Random rand = new Random();
		
		int m = numMines;
		
		while (m > 0)
		{
			num = rand.nextInt((rows * cols) - 1);
			int row = (num / cols);
			int col = (num % cols);

			if (mines[row][col] != 1 && !invalid.contains(num))
			{
				mines[row][col] = 1;
				m--;
			}
		}
		mineProx();
	}
	
	public void outputMineBoard()
	{
		System.out.println("Mine Board");
		
		System.out.print("   ");
		
		for (int i = 0; i < mines[0].length; i++)
		{
			if (i < 9)
			{
			System.out.print((i + 1) + "  ");
			}
			else
			{
				System.out.print((i + 1) + " ");
			}
		}
		
		System.out.println("");
		
		for (int i = 0; i < mines.length; i++)
		{
			for (int j = 0; j < mines[i].length; j++)
			{
				if (j == 0)
				{
					if (i < 9)
					{
						System.out.print((i + 1) + "  ");
					}
					else
					{
						System.out.print((i + 1) + " ");
					}
				}
				System.out.print(mines[i][j] + "  ");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	public void outputMineProx()
	{
		System.out.println("MineProx Board");
		
		System.out.print("   ");
		
		for (int i = 0; i < mineProx[0].length; i++)
		{
			if (i < 9)
			{
			System.out.print((i + 1) + "  ");
			}
			else
			{
				System.out.print((i + 1) + " ");
			}
		}
		
		System.out.println("");
		
		for (int i = 0; i < mineProx.length; i++)
		{
			for (int j = 0; j < mineProx[i].length; j++)
			{
				if (j == 0)
				{
					if (i < 9)
					{
						System.out.print((i + 1) + "  ");
					}
					else
					{
						System.out.print((i + 1) + " ");
					}
				}
				System.out.print(mineProx[i][j] + "  ");
			}
			System.out.println("");
		}
		System.out.println("");
	}
	
	public void zeros(int row, int col) {

		if (mineProx[row][col] == 0 && revealed[row][col] == false && flagged[row][col] == false)
		{
			revealed[row][col] = true;

			if (row + 1 < rows)
			{
				zeros(row + 1, col);
			}
			if (row - 1 >= 0)
			{
				zeros(row - 1, col);
			}
			if (col + 1 < cols)
			{
				zeros(row, col + 1);
			}
			if (col - 1 >= 0)
			{
				zeros(row, col - 1);
			}
			if (row + 1 < rows && col + 1 < cols)
			{
				zeros(row + 1, col + 1);
			}
			if (row + 1 < rows && col - 1 >= 0)
			{
				zeros(row + 1, col - 1);
			}
			if (row - 1 >= 0 && col + 1 < cols)
			{
				zeros(row - 1, col + 1);
			}
			if (row - 1 >= 0 && col - 1 >= 0)
			{
				zeros(row - 1, col - 1);
			}
		}
		else
		{
			if (flagged[row][col] == false)
			{
				revealed[row][col] = true;
			}
		}
	}
	
	public class Board extends JPanel {
	
		private static final long serialVersionUID = 1L;

		public void paint(Graphics g) {
			super.paint(g);
			
            if (mainMenu)
            {
                g.setColor(Color.black);
                g.fillRect(0, 0, sizeX, sizeY);
                g.setColor(Color.darkGray);
                
                //Get the Image
                String currentDirectory = System.getProperty("user.dir");
                String imagePath = currentDirectory + File.separator + "sweepermine.png";

                //Draw the logo on screen
                try 
                {
                    g.drawImage(ImageIO.read(new File(imagePath)), 208, 70, 800, 214, null);
                } 
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                
                //If the stats screen is up
                if (statsMenu)
                {
                    String fileName = "stats.txt";
                    
                    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) 
                    {
                        String line = br.readLine();

                        if (line != null) 
                        {
                            try (Scanner scnr = new Scanner(line)) 
                            {
                                for (int i = 0; i < 5; i++) 
                                {
                                    if (scnr.hasNextInt()) 
                                    {
                                        stats[i] = scnr.nextInt();
                                    }
                                    else
                                    {
                                        throw new IOException("Invalid input format");
                                    }
                                }

                                // Close the scanner
                                scnr.close();
                            }
                        } 
                        else
                        {
                            //If file isnt read, set all of the stats to zero
                            for (int i = 0; i < stats.length; i++)
                            {
                                stats[i] = 0;
                            }
                        }
                    } 
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    
                    //Draw the text for the different stats
                    g.setColor(Color.white);
                    g.setFont(new Font("Tahome", Font.BOLD, 40));
                    g.drawString("GAMES WON", sizeX / 2 - 50, sizeY / 2 - 60);
                    g.setColor(Color.green);
                    g.drawString("EASY", sizeX / 2 - 220, sizeY / 2 - 10);
                    g.setColor(Color.yellow);
                    g.drawString("MEDIUM", sizeX / 2 - 220, sizeY / 2 + 40);
                    g.setColor(Color.red);
                    g.drawString("HARD", sizeX / 2 - 220, sizeY / 2 + 90);
                    g.setColor(Color.white);
                    g.drawString("STREAK", sizeX / 2 - 430, sizeY / 2 - 30);
                    g.drawString("TOTAL", sizeX / 2 + 285, sizeY / 2 - 30);
                    
                    FontMetrics metrics = g.getFontMetrics(new Font("Tahome", Font.BOLD, 40));
                    
                    //Draw the stats numbers for games won
                    for (int i = 0; i < 3; i++)
                    {
                        int width = metrics.stringWidth(String.valueOf(stats[i]));
                        
                        g.drawString(String.valueOf(stats[i]), (sizeX / 2) + 85 - (width / 2), sizeY / 2  - 10 + (50 * i));
                    }
                    //Draw the stats for total games and streak
                    int width = metrics.stringWidth(String.valueOf(stats[3]));
                    g.drawString(String.valueOf(stats[3]), (sizeX / 2) - 350 - (width / 2), sizeY / 2 + 45);
                    
                    width = metrics.stringWidth(String.valueOf(stats[4]));
                    g.drawString(String.valueOf(stats[4]), (sizeX / 2) + 350 - (width / 2), sizeY / 2 + 45);
                    
                    //Stats box outline
                    g.setColor(Color.darkGray);
                    g.drawRect(sizeX / 2 - 450, sizeY / 2 - 125, 200, 250);
                    g.drawRect(sizeX / 2 - 250, sizeY / 2 - 125, 500, 250);
                    g.drawRect(sizeX / 2 + 250, sizeY / 2 - 125, 200, 250);
                    
                    //Back button
                    g.drawRect(sizeX / 2 - 180 - 4, sizeY / 2 + 140 - 4, 360 + 8, 80 + 8);
                    g.fillRect(sizeX / 2 - 180, sizeY / 2 + 140, 360, 80);
                    
                    //Check if mouse is over a button
                    if (mouseX >= sizeX / 2 - 180 && mouseX < sizeX / 2 + 180)
                    {
                        if (mouseY >= sizeY / 2 + 140 && mouseY < sizeY / 2 + 220)
                        {
                            if (g.getColor() == Color.darkGray)
                            {
                                g.setColor(Color.lightGray);
                            }
                        }
                        
                        g.fillRect(sizeX / 2 - 180, sizeY / 2 + 140, 360, 80);
                        g.setColor(Color.darkGray);
                    }
                    
                    g.setColor(Color.white);
                    g.setFont(new Font("Tahome", Font.BOLD, 40));
                    g.drawString("BACK", sizeX / 2 - 58, sizeY / 2 + 195);
                }
                //If the play screen is up
                else if (playMenu)
                {
                    g.setFont(new Font("Tahome", Font.BOLD, 40));

                    //Draw Small Medium Large
                    g.drawRect(sizeX / 2 - 405 - 4, sizeY / 2 - 100 - 4, 230 + 8, 70 + 8);
                    if (boardSize == 0)
                    {
                        g.setColor(Color.lightGray);
                    }
                    g.fillRect(sizeX / 2 - 405, sizeY / 2 - 100, 230, 70);
                    g.setColor(Color.darkGray);
                    
                    g.drawRect(sizeX / 2 - 115 - 4, sizeY / 2 - 100 - 4, 230 + 8, 70 + 8);
                    if (boardSize == 1)
                    {
                        g.setColor(Color.lightGray);
                    }
                    g.fillRect(sizeX / 2 - 115, sizeY / 2 - 100, 230, 70);
                    g.setColor(Color.darkGray);
                    
                    g.drawRect(sizeX / 2 + 175 - 4, sizeY / 2 - 100 - 4, 230 + 8, 70 + 8);
                    if (boardSize == 2)
                    {
                        g.setColor(Color.lightGray);
                    }
                    g.fillRect(sizeX / 2 + 175, sizeY / 2 - 100, 230, 70);
                    g.setColor(Color.darkGray);
                    
                    //Draw Easy Normal Hard
                    g.drawRect(sizeX / 2 - 405 - 4, sizeY / 2 - 4, 230 + 8, 70 + 8);
                    if (difficulty == 0)
                    {
                        g.setColor(Color.lightGray);
                    }
                    g.fillRect(sizeX / 2 - 405, sizeY / 2, 230, 70);
                    g.setColor(Color.darkGray);
                    
                    g.drawRect(sizeX / 2 - 115 - 4, sizeY / 2 - 4, 230 + 8, 70 + 8);
                    if (difficulty == 1)
                    {
                        g.setColor(Color.lightGray);
                    }
                    g.fillRect(sizeX / 2 - 115, sizeY / 2, 230, 70);
                    g.setColor(Color.darkGray);
                    
                    g.drawRect(sizeX / 2 + 175 - 4, sizeY / 2 - 4, 230 + 8, 70 + 8);
                    if (difficulty == 2)
                    {
                        g.setColor(Color.lightGray);
                    }
                    g.fillRect(sizeX / 2 + 175, sizeY / 2, 230, 70);
                    g.setColor(Color.darkGray);
                    
                    //Draw the text
                    g.setColor(Color.white);
                    g.drawString("SMALL", sizeX / 2 - 405 + 45, sizeY / 2 - 100 + 50);
                    g.drawString("MEDIUM", sizeX / 2 - 80, sizeY / 2 - 100 + 50);
                    g.drawString("LARGE", sizeX / 2 + 175 + 45, sizeY / 2 - 100 + 50);
                    
                    g.drawString("EASY", sizeX / 2 - 405 + 60, sizeY / 2 + 50);
                    g.drawString("NORMAL", sizeX / 2 - 87, sizeY / 2 + 50);
                    g.drawString("HARD", sizeX / 2 + 175 + 115 - 55, sizeY / 2 + 50);
                    
                    g.setColor(Color.darkGray);
                    
                    //Draw Back and Play buttons
                    g.drawRect(sizeX / 2 + 175 - 4, sizeY / 2 + 140 - 4, 300 + 8, 80 + 8);
                    g.fillRect(sizeX / 2 + 175, sizeY / 2 + 140, 300, 80);
                    
                    g.drawRect(sizeX / 2 - 475 - 4, sizeY / 2 + 140 - 4, 300 + 8, 80 + 8);
                    g.fillRect(sizeX / 2 - 475, sizeY / 2 + 140, 300, 80);
                    
                    //Check if mouse is over a button
                    if (mouseY >= sizeY / 2 + 140 && mouseY < sizeY / 2 + 220)
                    {
                        if (mouseX >= sizeX / 2 - 475 && mouseX < sizeX / 2 - 175)
                        {
                            if (g.getColor() == Color.darkGray)
                            {
                                g.setColor(Color.lightGray);
                            }
                        }
                        
                        g.fillRect(sizeX / 2 - 475, sizeY / 2 + 140, 300, 80);
                        g.setColor(Color.darkGray);
                        
                        if (mouseX >= sizeX / 2 + 175 && mouseX < sizeX / 2 + 475)
                        {
                            if (g.getColor() == Color.darkGray)
                            {
                                g.setColor(Color.lightGray);
                            }
                        }
                        
                        g.fillRect(sizeX / 2 + 175, sizeY / 2 + 140, 300, 80);
                        g.setColor(Color.darkGray);
                        
                    }
                    
                    g.setColor(Color.white);
                    g.drawString("BACK", sizeX / 2 - 383, sizeY / 2 + 195);
                    g.drawString("PLAY", sizeX / 2 + 275, sizeY / 2 + 195);
                }
                //At the main menu screen
                else
                {
                    //Draw Buttons
                    g.drawRect(sizeX / 2 - 180 - 4, sizeY / 2 - 80 - 4, 360 + 8, 80 + 8);
                    g.drawRect(sizeX / 2 - 180 - 4, sizeY / 2 + 30 - 4, 360 + 8, 80 + 8);
                    g.drawRect(sizeX / 2 - 180 - 4, sizeY / 2 + 140 - 4, 360 + 8, 80 + 8);
                    
                    g.fillRect(sizeX / 2 - 180, sizeY / 2 - 80, 360, 80);
                    g.fillRect(sizeX / 2 - 180, sizeY / 2 + 30, 360, 80);
                    g.fillRect(sizeX / 2 - 180, sizeY / 2 + 140, 360, 80);
                    
                    //Check if mouse is over a button
                    if (mouseX >= sizeX / 2 - 180 && mouseX < sizeX / 2 + 180)
                    {
                        if (mouseY >= sizeY / 2 - 80 && mouseY < sizeY / 2)
                        {
                            if (g.getColor() == Color.darkGray)
                            {
                                g.setColor(Color.lightGray);
                            }
                        }
                        
                        g.fillRect(sizeX / 2 - 180, sizeY / 2 - 80, 360, 80);
                        g.setColor(Color.darkGray);
                        
                        if (mouseY >= sizeY / 2 + 30 && mouseY < sizeY / 2 + 110)
                        {
                            if (g.getColor() == Color.darkGray)
                            {
                                g.setColor(Color.lightGray);
                            }
                        }
                        
                        g.fillRect(sizeX / 2 - 180, sizeY / 2 + 30, 360, 80);
                        g.setColor(Color.darkGray);
                        
                        if (mouseY >= sizeY / 2 + 140 && mouseY < sizeY / 2 + 220)
                        {
                            if (g.getColor() == Color.darkGray)
                            {
                                g.setColor(Color.lightGray);
                            }
                        }
                        
                        g.fillRect(sizeX / 2 - 180, sizeY / 2 + 140, 360, 80);
                        g.setColor(Color.darkGray);
                    }

                    g.setColor(Color.white);
                    g.setFont(new Font("Tahome", Font.BOLD, 40));
                    g.drawString("PLAY", sizeX / 2 - 50, sizeY / 2 - 25);
                    g.drawString("STATS", sizeX / 2 - 63, sizeY / 2 + 85);
                    g.drawString("QUIT", sizeX / 2 - 50, sizeY / 2 + 195);
                }
            }
            else
            {
    			//Print Background
    			g.setColor(Color.black);
    			g.fillRect(0, 0, sizeX, sizeY);
    			g.setColor(Color.darkGray);
    			g.drawRect(originX - spacing - 1, originY - spacing - 1, cols * tileSize + (cols - 1) * spacing + spacing * 2 + 1, rows * tileSize + (rows - 1) * spacing + spacing * 2 + 1); //Game Area Outline
    			g.drawRect(originX - spacing - 1, originY / 3 - spacing - 1, originX * 3 + (2 * spacing) + 1, originY - 66 + (2 * spacing) + 1); //Quit Button
    			g.drawRect((originX  * 5) - spacing - 1, originY / 3 - spacing - 1, originX * 3 + (2 * spacing) + 1, originY - 66 + (2 * spacing) + 1); //Reset Button
    			g.drawRect((originX - spacing - 1) + (cols * tileSize + (cols - 1) * spacing + spacing * 2 + 1) - (originX * 2) - (spacing * 4), originY / 3 - spacing - 1, originX * 2 + (2 * spacing) + 4, originY - 66 + (2 * spacing) + 1); //Flag Counter

    			//Check if mouse is over quit tile
                if (mouseX >= originX && mouseX < originX * 4)
                {
                    if (mouseY >= (originY / 3) && mouseY < (originY - 33))
                    {
                        if (g.getColor() == Color.darkGray)
                        {
                            g.setColor(Color.lightGray);
                        }
                    }
                }
                
                //Print Quit
                g.setFont(new Font("Tahome", Font.BOLD, 25));
                g.fillRect(originX, originY / 3, originX * 3, originY - 66);
                g.setColor(Color.white);
                g.drawString("QUIT" , originX + 45, originY - 40);
                
                g.setColor(Color.darkGray);
                
    			//Check if mouse is over reset tile
    			if (mouseX >= originX * 5 && mouseX < originX * 8)
    			{
    				if (mouseY >= (originY / 3) && mouseY < (originY - 33))
    				{
    					if (g.getColor() == Color.darkGray)
    					{
    						g.setColor(Color.lightGray);
    					}
    				}
    			}
    
    			//Print Reset
    			g.setFont(new Font("Tahome", Font.BOLD, 25));
    			g.fillRect(originX * 5, originY / 3, originX * 3, originY - 66);
    			g.setColor(Color.white);
    			g.drawString("RESET" , (originX * 5) + 33, originY - 40);
    			
    			//Print Flag Counter
    
    			g.setColor(Color.darkGray);
    			g.fillRect((originX - spacing - 1) + (cols * tileSize + (cols - 1) * spacing + spacing * 2 + 1) - (originX * 2) - (spacing * 2), originY / 3, originX * 2 + spacing, originY - 66);
    			
    			g.setColor(Color.red);
    			g.fillRect(sizeX - (3 * originX) - 4, originY / 2 - 2, 11, 3);
    			g.fillRect(sizeX - (3 * originX) - 1, originY / 2 - 4, 8, 2);
    			g.fillRect(sizeX - (3 * originX) - 1, originY / 2 + 1, 8, 2);
    			g.fillRect(sizeX - (3 * originX) + 2, originY / 2 + 3, 5, 2);
    			g.fillRect(sizeX - (3 * originX) + 2, originY / 2 - 6, 5, 2);
    			g.setColor(Color.black);
    			g.fillOval(sizeX - (3 * originX) - 5, originY / 2 + 7, 23, 7);
    			g.fillRect(sizeX - (3 * originX) + 7, originY / 3 + 7, 3, 20);
    			g.setColor(Color.white);
    			
    			//Check if flag counter is less than 2 digits, I.E need to space the number out more evenly.
    			if (totalFlags >= 10)
    			{
    				g.drawString(": " + String.valueOf(totalFlags) , sizeX - (2 * originX) - 21, originY - 41);
    			}
    			else
    			{
    				g.drawString(":  " + String.valueOf(totalFlags) , sizeX - (2 * originX) - 21, originY - 41);
    			}
    			
    			//Print Board
    			for (int i = 0; i < rows; i++)
    			{
    				for (int j = 0; j < cols; j++)
    				{
    					g.setColor(Color.darkGray);
    					
    					if (revealed[i][j] == true)
    					{
    						if (mines[i][j] == 1)
    						{
    							g.setColor(Color.red);
    							g.fillRect(originX + (spacing * j) + (tileSize * j), originY + (spacing * i) + (tileSize * i), tileSize, tileSize);
    							g.setColor(Color.black);
    							g.fillOval(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.24), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.24), (int) (tileSize * 0.52), (int) (tileSize * 0.52));
    							g.setColor(Color.white);
    							g.fillOval(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.34), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.34), (int) (tileSize * 0.14), (int) (tileSize * 0.14));
    						}
    						else
    						{
    							if (mineProx[i][j] != 0)
    							{
    								g.setFont(new Font("Tahome", Font.BOLD, (int) (tileSize * 0.5)));
    								
    								if (mineProx[i][j] == 1)
    									g.setColor(Color.blue);
    								if (mineProx[i][j] == 2)
    									g.setColor(Color.green);
    								if (mineProx[i][j] == 3)
    									g.setColor(Color.red);
    								if (mineProx[i][j] == 4)
    									g.setColor(Color.orange);
    								if (mineProx[i][j] == 5)
    									g.setColor(Color.yellow);
    								if (mineProx[i][j] == 6)
    									g.setColor(Color.cyan);
    								if (mineProx[i][j] == 7)
    									g.setColor(Color.magenta);
    								if (mineProx[i][j] == 8)
    									g.setColor(Color.pink);
    								
    								g.drawString(String.valueOf(mineProx[i][j]), originX + (tileSize * j) + (spacing * j) + (int) (tileSize * 0.38), originY + (tileSize * i) + (spacing * i) + (int) (tileSize * 0.68));
    							}
    						}
    					}
    					
    					//Check if mouse is over a gray tile spot
    					if (mouseX >= originX + (tileSize * j) + (spacing * j) && mouseX < originX + (tileSize * j) + (spacing * j) + tileSize)
    					{
    						if (mouseY >= originY + (tileSize * i) + (spacing * i) && mouseY < originY + (tileSize * i) + (spacing * i) + tileSize)
    						{
    							if (g.getColor() == Color.darkGray)
    							{
    								g.setColor(Color.lightGray);
    							}
    						}
    					}
    					
    					//If its not revealed or its flagged, paint normal square tile
    					if (revealed[i][j] == false || flagged[i][j] == true)
    					{
    						g.fillRect(originX + (spacing * j) + (tileSize * j), originY + (spacing * i) + (tileSize * i), tileSize, tileSize);
    					}
    					
    					//If it is flagged, print flag on tile
    					if (flagged[i][j] == true)
    					{
    						g.setColor(Color.red);
    						g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.24), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.4), (int) (tileSize * 0.3), (int) (tileSize * 0.08));
    						g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.32), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.34), (int) (tileSize * 0.24), (int) (tileSize * 0.08));
    						g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.32), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.46), (int) (tileSize * 0.24), (int) (tileSize * 0.08));
    						g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.4), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.28), (int) (tileSize * 0.16), (int) (tileSize * 0.08));
    						g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.4), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.52), (int) (tileSize * 0.16), (int) (tileSize * 0.08));
    						g.setColor(Color.black);
    						g.fillOval(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.26), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.7), (int) (tileSize * 0.5), (int) (tileSize * 0.16));
    						g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.52), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.24), (int) (tileSize * 0.06), (int) (tileSize * 0.5));
    					}
    					
    					//If either the player won or lost, display the final outcome at the top and prevent the player from modifying the board.
                        if (gameOver)
                        {
                            revealed[i][j] = true;
                            
                            //Check if the players flags were right or wrong. Green = correct, Yellow = wrong.
                            if (flagged[i][j] == true)
                            {
                                if (mines[i][j] == 1)
                                {
                                    g.setColor(Color.green);
                                }
                                else
                                {
                                    g.setColor(Color.yellow);
                                }
                                
                                g.fillRect(originX + (spacing * j) + (tileSize * j), originY + (spacing * i) + (tileSize * i), tileSize, tileSize);
                                
                                g.setColor(Color.red);
                                g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.24), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.4), (int) (tileSize * 0.3), (int) (tileSize * 0.08));
                                g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.32), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.34), (int) (tileSize * 0.24), (int) (tileSize * 0.08));
                                g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.32), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.46), (int) (tileSize * 0.24), (int) (tileSize * 0.08));
                                g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.4), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.28), (int) (tileSize * 0.16), (int) (tileSize * 0.08));
                                g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.4), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.52), (int) (tileSize * 0.16), (int) (tileSize * 0.08));
                                g.setColor(Color.black);
                                g.fillOval(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.26), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.7), (int) (tileSize * 0.5), (int) (tileSize * 0.16));
                                g.fillRect(originX + (spacing * j) + (tileSize * j) + (int) (tileSize * 0.52), originY + (spacing * i) + (tileSize * i) + (int) (tileSize * 0.24), (int) (tileSize * 0.06), (int) (tileSize * 0.5));
                            }
                        }
                        else
                        {
        					//Check if player has won the game. I.E if every mine is either covered with a flag or they removed every tile that isn't a mine.
        					if (checkWin())
        					{
        						g.setColor(Color.white);
        						g.setFont(new Font("Tahome", Font.BOLD, 35));
        						g.drawString("You Win!", (sizeX / 2) - (172 / 2), 60);
        						gameOver = true;
        						
        						stats[difficulty]++; //Increment games won for the given difficulty
        						stats[4]++; //Increment games played
        						stats[3]++; //Increment current streak
        						
        						rewriteStats();
        					}
        					//Else, check if the player has lost. I.E if they have hit a mine.
        					else if (checkLoss())
        					{
        						g.setColor(Color.white);
        						g.setFont(new Font("Tahome", Font.BOLD, 35));
        						g.drawString("You Lost!", (sizeX / 2) - (172 / 2), 60);
        						gameOver = true;
        						
        						stats[3] = 0; //Reset the streak
        						stats[4]++; //Increment games played
        						
        						rewriteStats();
        					}
                        }
    				}
    			}
            }
		}
	}
	
	public class Move implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX() - 8;
			mouseY = e.getY() - 31;
		}
		
	}
	
	public class Click implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
	
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
		    
		    //If we are at the main menu
		    if (mainMenu)
		    {
		        if (e.getButton() == MouseEvent.BUTTON1)
		        {
		            //Stats menu is up
		            if (statsMenu)
		            {
		                if (mouseX >= sizeX / 2 - 180 && mouseX < sizeX / 2 + 180)
                        {
                            if (mouseY >= sizeY / 2 + 140 && mouseY < sizeY / 2 + 220)
                            {
                                statsMenu = false;
                            }
                        }
		            }
		            //Play menu is up
		            else if (playMenu)
                    {
		                //Check if any of the options were clicked
		                if (mouseY >= sizeY / 2 - 100 && mouseY < sizeY / 2 - 30)
		                {
		                    if (mouseX >= sizeX / 2 - 405 && mouseX < sizeX / 2 - 175)
		                    {
		                        boardSize = 0;
		                    }
		                    if (mouseX >= sizeX / 2 - 115 && mouseX < sizeX / 2 + 115)
                            {
                                boardSize = 1;
                            }
		                    if (mouseX >= sizeX / 2 + 175 && mouseX < sizeX / 2 + 405)
                            {
                                boardSize = 2;
                            }
		                }
		                else if (mouseY >= sizeY / 2 && mouseY < sizeY / 2 + 70)
                        {
                            if (mouseX >= sizeX / 2 - 405 && mouseX < sizeX / 2 - 175)
                            {
                                difficulty = 0;
                            }
                            if (mouseX >= sizeX / 2 - 115 && mouseX < sizeX / 2 + 115)
                            {
                                difficulty = 1;
                            }
                            if (mouseX >= sizeX / 2 + 175 && mouseX < sizeX / 2 + 405)
                            {
                                difficulty = 2;
                            }
                        }
		                
		                //Check the bottom buttons
		                if (mouseY >= sizeY / 2 + 140 && mouseY < sizeY / 2 + 220)
	                    {
		                    //If back is clicked
		                    if (mouseX >= sizeX / 2 - 475 && mouseX < sizeX / 2 - 175)
	                        {
	                            playMenu = false;
	                        }

	                        //If play is clicked
	                        if (mouseX >= sizeX / 2 + 175 && mouseX < sizeX / 2 + 475)
	                        {
	                            switch(boardSize) 
	                            {
	                                case 0: cols = 15; rows = 9; break;
	                                case 1: cols = 20; rows = 12; break;
	                                case 2: cols = 30; rows = 18; break;
	                            }
	                            switch(difficulty) 
	                            {
                                    case 0: numMines = (int) ((cols * rows) * 0.13); break;
                                    case 1: numMines = (int) ((cols * rows) * 0.19); break;
                                    case 2: numMines = (int) ((cols * rows) * 0.25); break;
                                }
	                            
	                            mines = new int[rows][cols];
	                            mineProx = new int[rows][cols];
	                            flagged = new boolean[rows][cols];
	                            revealed = new boolean[rows][cols];
	                            
	                            counter = 0;
	                            totalFlags = numMines;
	                            tileSize = (sizeX - 100 - ((cols - 1) * spacing)) / cols;
	                            invalid = new ArrayList<>();
	                            
	                            playMenu = false;
	                            mainMenu = false;
	                        }
	                    }
                    }
                    //Main menu is up
		            else
		            {
    		            if (mouseX >= sizeX / 2 - 180 && mouseX < sizeX / 2 + 180)
                        {
                            if (mouseY >= sizeY / 2 - 80 && mouseY < sizeY / 2)
                            {
                                playMenu = true;
                            }
                            
                            if (mouseY >= sizeY / 2 + 30 && mouseY < sizeY / 2 + 110)
                            {
                                statsMenu = true;
                            }
    
                            if (mouseY >= sizeY / 2 + 140 && mouseY < sizeY / 2 + 220)
                            {
                                System.exit(0);
                            }
                        }
		            }
		        }
		    }
		    else
		    {
    		    if (e.getButton() == MouseEvent.BUTTON1)
                {
                    if (firstMove == false)
                    {
                        if (inBoxX() >= 0)
                        {
                            if (inBoxY() >= 0)
                            {
                                fillMines(inBoxX(), inBoxY());
                                zeros(inBoxX(), inBoxY());
                                firstMove = true;
                            }
                        }
                    }
                    else
                    {
                        if (gameOver == false)
                        {
                            if (inBoxX() >= 0)
                            {
                                if (inBoxY() >= 0)
                                {
                                    if (flagged[inBoxX()][inBoxY()] == true)
                                    {
                                        flagged[inBoxX()][inBoxY()] = false;
                                        totalFlags++;
                                    }
                                    
                                    else if (revealed[inBoxX()][inBoxY()] == false)
                                    {
                                        if (mineProx[inBoxX()][inBoxY()] == 0)
                                        {
                                            zeros(inBoxX(), inBoxY());
                                        }
                                        revealed[inBoxX()][inBoxY()] = true;
                                    }
                                }
                            }
                        }
                        
                        //Check if reset was clicked
                        if (mouseX >= originX * 5 && mouseX < originX * 8)
                        {
                            if (mouseY >= (originY / 3) && mouseY < (originY - 33))
                            {
                                reset();
                            }
                        }
                    }
                    
                    //Check if quit was clicked
                    if (mouseX >= originX && mouseX < originX + (originX * 3))
                    {
                        if (mouseY >= (originY / 3) && mouseY < (originY - 33))
                        {
                            mainMenu = true;
                            reset();
                        }
                    }
                }
                else if (e.getButton() == MouseEvent.BUTTON3)
                {
                    if (inBoxX() >= 0)
                    {
                        if (inBoxY() >= 0)
                        {
                            if (!revealed[inBoxX()][inBoxY()] && firstMove == true && flagged[inBoxX()][inBoxY()] == false && totalFlags > 0)
                            {
                                flagged[inBoxX()][inBoxY()] = true;
                                totalFlags--;
                            }
                        }
                    }
                }
		    }
		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}
	
	private void reset()
	{
		counter = 0;
		totalFlags = numMines;
		
		invalid = new ArrayList<>();
		
		firstMove = false;
		gameOver = false;

		mines = new int[rows][cols];
		mineProx = new int[rows][cols];
		flagged = new boolean[rows][cols];
		revealed = new boolean[rows][cols];
	}
	
	private boolean checkWin()
	{
		int count = 0;
		
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (mines[i][j] == 0 && revealed[i][j] == false)
				{
					break;
				}
			}
		}

		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (count == numMines)
					return true;
				
				if (mines[i][j] == 1 && flagged[i][j] == true)
				{
					count++;
				}
			}
		} 
		
		return false;
	}
	
	private boolean checkLoss()
	{
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (mines[i][j] == 1 && revealed[i][j] == true)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private int inBoxX()
	{
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (mouseX >= originX + (tileSize * j) + (spacing * j) && mouseX < originX + (tileSize * j) + (spacing * j) + tileSize)
				{
					if (mouseY >= originY + (tileSize * i) + (spacing * i) && mouseY < originY + (tileSize * i) + (spacing * i) + tileSize)
					{
						return i;
					}
				}
			}
		}
		return -1;
	}
	
	private int inBoxY()
	{
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (mouseX >= originX + (tileSize * j) + (spacing * j) && mouseX < originX + (tileSize * j) + (spacing * j) + tileSize)
				{
					if (mouseY >= originY + (tileSize * i) + (spacing * i) && mouseY < originY + (tileSize * i) + (spacing * i) + tileSize)
					{
						return j;
					}
				}
			}
		}
		return -1;
	}
	
	private void rewriteStats()
	{
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("stats.txt"))) 
	    {
            // Format the array elements in the desired format
            String formattedString = String.format("%d %d %d %d %d", stats[0], stats[1], stats[2], stats[3], stats[4]);

            // Write the formatted string to the file
            writer.write(formattedString);

        } 
	    catch (IOException e) 
	    {
            e.printStackTrace();
        }
	}
}


