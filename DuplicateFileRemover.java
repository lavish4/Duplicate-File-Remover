import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.security.MessageDigest;
import java.nio.file.*;

public class DuplicateFileRemover extends JPanel implements ActionListener {
    
    JList<String> list1;
	DefaultListModel<String> listModel;
    JButton browsebutton1;
    JButton browsebutton2;
    JButton Delete;
    JTextArea area;
    JFileChooser fc;
    JTextField t1,t2;  
	static int dupCount = 0;
    File file1,file2;
    String temp=new String();
    String temp1=new String();
    public static HashMap<String, String> hashmap = new HashMap<String, String>();
	public static HashMap<String, String> dupHashMap = new HashMap<String, String>();
    
    public DuplicateFileRemover(){
        super(new BorderLayout());
        //Create list model.
        listModel = new DefaultListModel<>();
        list1 = new JList<>(listModel);
        list1.setSize(100, 100);
        list1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        
        //Create the area.
        area = new JTextArea(100,100);
        area.setMargin(new Insets(5,5,5,5));
        area.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(area);
        logScrollPane.add(list1);
        
        //Create a file chooser
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        
        browsebutton1 = new JButton("FOLDER1:         Browse");
        browsebutton2 = new JButton("FOLDER2:         Browse");
        Delete = new JButton("Delete");
        browsebutton1.addActionListener(this);
        browsebutton2.addActionListener(this);
        Delete.addActionListener(this);
        
        JPanel buttonPanel = new JPanel(); //use FlowLayout
        buttonPanel.add(browsebutton1);
        buttonPanel.add(browsebutton2);
        buttonPanel.add(Delete);
        add(buttonPanel, BorderLayout.PAGE_START);
        add(new JScrollPane(list1), BorderLayout.CENTER);
    }
    
    //Checking for Duplicate Files
    
    public void dupScan(String folderToScan){
		try
		{
			File dir = new File(folderToScan);
			File[] fileList = dir.listFiles();
			for(File oneFile : fileList){
				if(oneFile.isFile())
				{
					MessageDigest md = MessageDigest.getInstance("MD5");
					byte[] fileBytes = Files.readAllBytes(oneFile.toPath());
					String fileString = Base64.getEncoder().encodeToString(md.digest(fileBytes));
					
					if(hashmap.containsKey(fileString))
					{ 
						String original = hashmap.get(fileString);
						String duplicate = oneFile.getAbsolutePath();
						listModel.addElement("Duplicate File : " + duplicate);
						dupHashMap.put(Integer.toString(dupCount++),duplicate);
					}
					else
					{
						hashmap.put(fileString, oneFile.getAbsolutePath());
						 listModel.addElement("Unique File : " + oneFile.toPath());
					}
				}
                else if(oneFile.isDirectory())
				{
					dupScan(oneFile.getAbsolutePath());
				}
			}
		}
		catch (Exception x)
		{
			System.out.println("Error Message : "  + x.getMessage());
		}
	}
    
    //function to delete file
	public void delDuplicates(boolean state)
	{
		if(state == true)
		{
			try
			{
				int numOfDelFiles = 0;
				for(String dupHashName : dupHashMap.keySet())
				{
					Path pathOfFile = Paths.get(dupHashMap.get(dupHashName));
					String dupFileName = dupHashMap.get(dupHashName);
					Files.delete(pathOfFile);
					listModel.addElement("File '" + dupFileName + "' Is Deleted");
					numOfDelFiles++;
				}
				listModel.addElement("---------------------------------------------------------------------------------");
				listModel.addElement(numOfDelFiles + " of Duplicate Files Are Deleted");
			}
			catch(Exception x)
			{
				System.out.println("Error Message : "  + x.getMessage());
			}
		}
	}
    
    public void actionPerformed(ActionEvent e){
        //Handle Browse button1 action.
        if (e.getSource() == browsebutton1) {
        	int returnVal = fc.showOpenDialog(DuplicateFileRemover.this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
                file2 = fc.getSelectedFile();
                temp1=file2.getAbsolutePath();
                listModel.addElement("Looking for Duplicates in Folder Name: " + file2.getName() + " at location " + temp1);
                dupScan(temp1);
            }
            
        //Handle Browse button2 action.
        if (e.getSource() == browsebutton2) {
        	int returnVal = fc.showOpenDialog(DuplicateFileRemover.this);
            if (returnVal == JFileChooser.APPROVE_OPTION){
                file1= fc.getSelectedFile();
                temp=file1.getAbsolutePath();
                listModel.addElement("Looking for Duplicates in Folder Name: " + file1.getName() + " at location " + temp);
                dupScan(temp);
            }
        }
        
        if(e.getSource() == Delete){
            delDuplicates(true);
        }
    }   
        
        
    private static void createAndShowGUI() {
        //Create window.
        JFrame frame = new JFrame("Duplicate File Remove");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content.
        frame.add(new DuplicateFileRemover());

        //Display window.
        frame.pack();
        frame.setVisible(true);
        frame.setSize(700, 500);
    }
    
    
    public static void main(String[] args) {

        // Creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                createAndShowGUI();
            }
        });
    }    
}