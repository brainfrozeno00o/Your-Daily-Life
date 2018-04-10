package maze;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

public class DailyLifeGUI extends JFrame{ 
	JButton command, checkInventory, help, showLove, giveUp; 
	JLabel putHere;
	JTextField inputCommand;
	JTextArea outputIt;
	JPanel commandCenter, southButtons;
	
	MazeMaker maze = new MazeMaker();
	Font mySteamFont = new Font("Segoe UI", Font.PLAIN, 12);
	Font mySteamButtonFont = new Font("Segoe UI", Font.BOLD, 11);
	Color commonBG = new Color(49,49,49);
	Color textBG = new Color(103,103,103);
	Color textFG = new Color(254,254,254);
	Color highlightBG = new Color(69,69,69);
	
	public static void main(String[] args) throws Exception{
		DailyLifeGUI mahlife = new DailyLifeGUI(); 
		mahlife.setSize(600, 610);
		mahlife.setLocationRelativeTo(null);
		mahlife.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mahlife.setVisible(true);
		mahlife.setTitle("Your Daily Life");
	}
	
	public DailyLifeGUI(){
		setLayout(new BorderLayout());
		createCommandCenter();
		createTextArea();
		createSouthButtons();
		this.getRootPane().setDefaultButton(command);
	}
	
	private void createCommandCenter(){
		commandCenter = new JPanel();
		commandCenter.setBackground(commonBG);
		commandCenter.setLayout(new FlowLayout(1,15,10));
		add(commandCenter, "North");

		putHere = new JLabel("Input Command Here: ");
		putHere.setForeground(textFG);
		putHere.setFont(mySteamButtonFont);
		commandCenter.add(putHere);

		inputCommand = new JTextField(27);
		inputCommand.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e){}
			@Override
			public void focusLost(FocusEvent e){requestFocus();}
		});
		inputCommand.setBackground(textBG);
		inputCommand.setFont(mySteamFont);
		inputCommand.setForeground(textFG);
		inputCommand.setBorder(null);
		commandCenter.add(inputCommand);

		command = new JButton("Run Command");
		command.setFocusPainted(false);
		command.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				outputIt.append("\n");
				try{
					if(inputCommand.getText().trim().isEmpty()){
						outputIt.append("What are you trying to do?\n");
					}else{
						if(inputCommand.getText().equals("exit")) System.exit(0);
						else{
							if(inputCommand.getText().contains("go")) outputIt.append(maze.move(inputCommand.getText()));
							else outputIt.append(maze.execute(inputCommand.getText()));
							if(maze.checkIfDead()){
								command.setEnabled(false);
								checkInventory.setEnabled(false);
								help.setEnabled(false);
								showLove.setEnabled(true);
								giveUp.setText("Exit");
							}
						}						
					}
					inputCommand.setText("");
					outputIt.setCaretPosition(outputIt.getDocument().getLength());
				}catch(Exception e){}
			}
		});
		command.setBackground(commonBG);
		command.setFont(mySteamButtonFont);
		command.setForeground(textFG);
		command.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		command.getModel().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				ButtonModel model = (ButtonModel) e.getSource();
				if(model.isRollover()) command.setBackground(highlightBG);
				else command.setBackground(commonBG);
			}
		});
		commandCenter.add(command);	
	}
	
	private void createTextArea(){
		outputIt = new JTextArea(25,25);
		outputIt.setBackground(textBG);
		outputIt.setFont(mySteamFont);
		outputIt.setForeground(textFG);
		outputIt.setEditable(false);
		outputIt.setLineWrap(true);
		try{outputIt.append(maze.load());}catch(Exception e){}
		Border border = BorderFactory.createLineBorder(new Color(31,31,31), 5);
		outputIt.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(15,15,15,15)));
		JScrollPane scroll = new JScrollPane(outputIt);
		scroll.setBorder(null);
		this.add(scroll);	
	}
	
	private void createSouthButtons(){
		southButtons = new JPanel();
		southButtons.setBackground(commonBG);
		southButtons.setLayout(new FlowLayout(1,40,10));
		add(southButtons, "South");

		checkInventory = new JButton("Check Inventory");
		checkInventory.setFocusPainted(false);
		checkInventory.setBackground(commonBG);
		checkInventory.setFont(mySteamButtonFont);
		checkInventory.setForeground(textFG);
		checkInventory.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
		checkInventory.getModel().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				ButtonModel model = (ButtonModel) e.getSource();
				if(model.isRollover()) checkInventory.setBackground(highlightBG);
				else checkInventory.setBackground(commonBG);
			}
		});
		checkInventory.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					outputIt.append("\n");
					outputIt.append(maze.execute("inventory"));
					outputIt.setCaretPosition(outputIt.getDocument().getLength());
				}catch(Exception e){}
			}
		});
		southButtons.add(checkInventory);

		help = new JButton("Help");
		help.setFocusPainted(false);
		help.setBackground(commonBG);
		help.setFont(mySteamButtonFont);
		help.setForeground(textFG);
		help.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
		help.getModel().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				ButtonModel model = (ButtonModel) e.getSource();
				if(model.isRollover()) help.setBackground(highlightBG);
				else help.setBackground(commonBG);
			}
		});
		help.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					outputIt.append("\n");
					outputIt.append(maze.execute("help"));
					outputIt.setCaretPosition(outputIt.getDocument().getLength());
				}catch(Exception e){}
			}
		});
		southButtons.add(help);

		showLove = new JButton("Show The Love");
		showLove.setFocusPainted(false);
		showLove.setEnabled(false);
		showLove.setBackground(commonBG);
		showLove.setFont(mySteamButtonFont);
		showLove.setForeground(textFG);
		showLove.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
		showLove.getModel().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				ButtonModel model = (ButtonModel) e.getSource();
				if(model.isRollover()) showLove.setBackground(highlightBG);
				else showLove.setBackground(commonBG);
			}
		});
		showLove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				outputIt.append("\n");
				outputIt.append("We think Eclipse is the best IDE in the world.\n");
				outputIt.setCaretPosition(outputIt.getDocument().getLength());
			}
		});
		southButtons.add(showLove);	

		giveUp = new JButton("I Give Up");
		giveUp.setFocusPainted(false);
		giveUp.setBackground(commonBG);
		giveUp.setFont(mySteamButtonFont);
		giveUp.setForeground(textFG);
		giveUp.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
		giveUp.getModel().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				ButtonModel model = (ButtonModel) e.getSource();
				if(model.isRollover()) giveUp.setBackground(highlightBG);
				else giveUp.setBackground(commonBG);
			}
		});
		giveUp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.exit(0);
			}
		});
		southButtons.add(giveUp);
	}
}