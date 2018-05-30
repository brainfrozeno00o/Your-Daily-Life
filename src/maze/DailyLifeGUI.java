package maze;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

public class DailyLifeGUI{
	JFrame mahlife = new JFrame("Your Daily Life");
	JButton command, checkInventory, help, runIt, showLove, giveUp; 
	JLabel putHere;
	JTextField inputCommand;
	JTextArea outputIt;
	JPanel commandCenter, southButtons;
	
	Font mySteamFont = new Font("Segoe UI", Font.PLAIN, 12);
	Font mySteamButtonFont = new Font("Segoe UI", Font.BOLD, 11);
	Color commonBG = new Color(49,49,49);
	Color textBG = new Color(103,103,103);
	Color textFG = new Color(254,254,254);
	Color highlightBG = new Color(69,69,69);

	State state;
	Strategy strat;
	DailyLifeGUI gui;
	
	public static void main(String[] args) throws Exception{
		DailyLifeGUI game = new DailyLifeGUI();
		game.gui = game;
	}
	
	public DailyLifeGUI(){
		state = new RegisterUser(false);
		strat = new TypeCommand();
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				mahlife.setLayout(new BorderLayout());
				createCommandCenter();
				createTextArea();
				createSouthButtons();
				mahlife.getRootPane().setDefaultButton(command);
				mahlife.addKeyListener(new KeyListener(){
					public void keyTyped(KeyEvent e){
						String addToString = inputCommand.getText();
						inputCommand.setText(addToString += e.getKeyChar());
						inputCommand.requestFocus();
					}
					public void keyPressed(KeyEvent e){}
					public void keyReleased(KeyEvent e){}
				});
				mahlife.setFocusable(true);
				mahlife.setFocusTraversalKeysEnabled(false);
				mahlife.setSize(600, 610);
				mahlife.setLocationRelativeTo(null);
				mahlife.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mahlife.setVisible(true);
			}
		});
	}

	public void setCurrState(State state){this.state = state;}

	public void setCurrStrat(Strategy strat){this.strat = strat;}

	public State getCurrState(){return state;}

	public Strategy getCurrStrat(){return strat;}

	public void playable(){
		checkInventory.setEnabled(true);
		command.setEnabled(true);
		help.setEnabled(true);
		runIt.setEnabled(true);
		giveUp.setText("Quit");		
	}

	public void unplayable(){
		checkInventory.setEnabled(false);
		help.setEnabled(false);
		runIt.setEnabled(false);
		giveUp.setText("Exit");
	}

	public void oneCommand() throws Exception{
		String commandThis = (String) getCurrStrat().getOperation(inputCommand.getText(), gui);
		if(commandThis == null) outputIt.append("What are you trying to do?\n");
		else {
			outputIt.append(commandThis);
			if(commandThis.equals("Thanks for playing!\n")) System.exit(0);
		}
	}

	@SuppressWarnings("unchecked")
	public void manyCommands() throws Exception{
		ArrayList<String> commands = (ArrayList<String>) getCurrStrat().getOperation(inputCommand.getText(), gui);
		if(commands != null) {
			for(String executeCommand : commands) {
				outputIt.append(executeCommand + "\n");
				if(executeCommand.equals("Thanks for playing!\n")) System.exit(0);
			}
		}else outputIt.append("No file found.\n");
	}
	
	private void createCommandCenter(){
		commandCenter = new JPanel();
		commandCenter.setBackground(commonBG);
		commandCenter.setLayout(new FlowLayout(1,15,10));
		mahlife.add(commandCenter, "North");

		putHere = new JLabel("Input Command Here: ");
		putHere.setForeground(textFG);
		putHere.setFont(mySteamButtonFont);
		commandCenter.add(putHere);

		inputCommand = new JTextField(27);
		inputCommand.setBackground(textBG);
		inputCommand.setFont(mySteamFont);
		inputCommand.setForeground(textFG);
		inputCommand.setBorder(null);
		commandCenter.add(inputCommand);

		command = new JButton("Run Command");
		command.setFocusPainted(false);
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
		command.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				outputIt.append("\n");
				try{
					switch(getCurrState().stateNumber()){
						case 0:
							if(getCurrStrat().getStratNumber() == 0) oneCommand(); else manyCommands(); 
							break;
						case 1:
							if(getCurrStrat().getStratNumber() == 0) oneCommand(); else manyCommands(); 
							if(((MazeMaker) state).checkIfDead()) unplayable(); else playable();			
							break;
					}
					inputCommand.setText("");
					outputIt.setCaretPosition(outputIt.getDocument().getLength());
					inputCommand.requestFocus();
				}catch(Exception e){}
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
		outputIt.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e){inputCommand.requestFocus();}
			@Override
			public void focusLost(FocusEvent e){}
		});

		try{outputIt.append(state.startIt());}catch(Exception e){}
		
		Border border = BorderFactory.createLineBorder(new Color(31,31,31), 5);
		outputIt.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(15,15,15,15)));
		JScrollPane scroll = new JScrollPane(outputIt);
		scroll.setBorder(null);
		mahlife.add(scroll);	
	}
	
	private void createSouthButtons(){
		southButtons = new JPanel();
		southButtons.setBackground(commonBG);
		southButtons.setLayout(new FlowLayout(1,40,10));
		mahlife.add(southButtons, "South");

		checkInventory = new JButton("Check Inventory");
		checkInventory.setFocusPainted(false);
		checkInventory.setEnabled(false);
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
					outputIt.append((String) getCurrStrat().getOperation("inventory", gui));
					outputIt.setCaretPosition(outputIt.getDocument().getLength());
					inputCommand.requestFocus();
				}catch(Exception e){}
			}
		});
		southButtons.add(checkInventory);

		help = new JButton("Help");
		help.setFocusPainted(false);
		help.setEnabled(false);
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
					outputIt.append((String) getCurrStrat().getOperation("help", gui));
					outputIt.setCaretPosition(outputIt.getDocument().getLength());
					inputCommand.requestFocus();
				}catch(Exception e){}
			}
		});
		southButtons.add(help);

		showLove = new JButton("Show The Love");
		showLove.setFocusPainted(false);
		showLove.setEnabled(true);
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
				try{
					outputIt.append("\n");
					if(((MazeMaker) state).checkIfDead()) outputIt.append("We think Eclipse is the best IDE in the world.\n");
					else outputIt.append((String) getCurrStrat().getOperation("reload", gui));
					outputIt.setCaretPosition(outputIt.getDocument().getLength());
					inputCommand.requestFocus();
				}catch(Exception e){}
			}
		});
		southButtons.add(showLove);	

		runIt = new JButton("Run");
		runIt.setFocusPainted(false);
		runIt.setEnabled(true);
		runIt.setBackground(commonBG);
		runIt.setFont(mySteamButtonFont);
		runIt.setForeground(textFG);
		runIt.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
		runIt.getModel().addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				ButtonModel model = (ButtonModel) e.getSource();
				if(model.isRollover()) runIt.setBackground(highlightBG);
				else runIt.setBackground(commonBG);
			}
		});
		runIt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				try{
					outputIt.append("\n");
					outputIt.append((String) getCurrStrat().getOperation("run", gui));
					outputIt.setCaretPosition(outputIt.getDocument().getLength());
					inputCommand.requestFocus();
				}catch(Exception e){}
			}
		});
		southButtons.add(runIt);

		giveUp = new JButton("Quit");
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
				try{if(getCurrState().stateNumber() == 1) ((MazeMaker) state).quit();}
				catch(Exception e){} finally{System.exit(0);}
			}
		});
		southButtons.add(giveUp);
	}
}