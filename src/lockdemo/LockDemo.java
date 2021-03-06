package lockdemo;

import java.io.File;
import java.io.FileWriter;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 *
 * @author vrosinac
 */

public class LockDemo {

	public static void main(String[] args) {
		// Let's create a counter and shared it between three threads
		// Since HTMLGenerator needs a lock to protect its getCount() method
		// we are giving it a ReentrantLock.
                try
                {
                
                    FileHandler handler = new FileHandler("default.log");
                    handler.setFormatter(new SimpleFormatter());
                    Logger logger = Logger.getLogger("lockdemo");
                    logger.addHandler(handler);
                


		String input ="elloe" ;
		String output = "hello" ;
		if (args.length > 0) {
			if (!args[0].isEmpty()) {
				 input = args[0];
			//	String output = args[1];
			//	 input = "C:\\Users\\u720960\\Downloads\\eclipse\\etest2.xml";
				  
			} else {
					
					logger.severe("please provide xml file with list of environments");
					
			}
			if (!args[1].isEmpty()) {
				output = args[1];
			}
			else {
				
				logger.severe("please provide name of output html file");
				
			}
		}
		else {
			
			logger.severe("please provide xml file with list of environments");
			logger.severe("please provide name of output html file");
			System.exit(0);
			
		}
                
		String sout = "";
		try {

			logger.info("reading from: " + input + " writingn to: "
					+ output);

			// Get Document Builder
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Build Document
			Document document = builder.parse(new File(input));

			// Normalize the XML Structure; It's just too important !!
			document.getDocumentElement().normalize();

			// Here comes the root node
			Element root = document.getDocumentElement();
			logger.info(root.getNodeName());

			// Get all envs
			NodeList nList = document.getElementsByTagName("environment");
			sout = sout + "<h1>TI available environments </h1>";

			DateTimeFormatter dtf = DateTimeFormatter
					.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			Calendar c = Calendar.getInstance();
			TimeZone tz = c.getTimeZone();
			String stz = tz.getDisplayName();

			sout = sout
					+ "<br>"
					+ "(last update: "
					+ dtf.format(now)
					+ " "
					+ stz
					+ ")<br> please reach jamesalexander.johnfuller@finastra.com, piotr.weglarz@finastra.com or vincent.rosinach@finastra.com if the page stops refreshing every 5 minutes<br><br>"; // 2016/11/16
																																														// 12:08:43
			sout = sout + "<style>table { font-size: 15px;} </style>";
			final HTMLGenerator myHTMLGenerator = new HTMLGenerator(
					new ReentrantLock(), sout); // Task to be executed by each
												// thread

			int size = nList.getLength();
			Thread threads[] = new Thread[size];
			Runnable runables[] = new Runnable[size];

			for (int i = 0; i < nList.getLength(); i++) {
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) // otherwise do
																// nothing
				{
					// Print each env detail
					Element eElement = (Element) node; // / we will need to pass
														// the element down
					// or to extract all its values and pass then down.... pass
					// rather the elt down
					// now we loop rouond the xml elements, one per thread
					// we don't know how many elements there will be .... so new
					// each time + copy nto thread by value
					// COUNTER TEMP - 1 array of xmlElement and one array of
					// ProcessElement

					ElementFields eltfields = new ElementFields();
					eltfields.environmentName = eElement
							.getElementsByTagName("environmentName").item(0)
							.getTextContent();
					
					if (eElement.getElementsByTagName("inuse") 	//loop that flter enviroments by 
							.item(0) != null) {					//that what we already use 
						
					// the next four tags are optional
					if (eElement.getElementsByTagName("protected_owner")
							.item(0) != null) {
						eltfields.protected_owner = eElement
								.getElementsByTagName("protected_owner")
								.item(0).getTextContent();
					}
                                        
                                        if (eElement.getElementsByTagName("prio")
							.item(0) != null) {
						eltfields.priorityEnvironment = eElement
								.getElementsByTagName("prio")
								.item(0).getTextContent();
					}
                                        
                                        if (eElement.getElementsByTagName("customized")
							.item(0) != null) {
						eltfields.customizedEnvironment = eElement
								.getElementsByTagName("customized")
								.item(0).getTextContent();
					}
                                        
                                        if (eElement.getElementsByTagName("customerdatabase")
							.item(0) != null) {
						eltfields.customerDatabase = eElement
								.getElementsByTagName("customerdatabase")
								.item(0).getTextContent();
					}
                                        
                                        if (eElement.getElementsByTagName("instructions")
							.item(0) != null) {
                                            
						String instructions = eltfields.instructions = eElement
								.getElementsByTagName("instructions")
								.item(0).getTextContent();
                                                instructions = instructions.replaceAll("(\r\n|\n)", "<br />");
                                                eltfields.instructions = instructions;
                                        }
                                        
                                        if (eElement.getElementsByTagName("folderlocations")
							.item(0) != null) {
                                            
						String folderLocations =  eElement
								.getElementsByTagName("folderlocations")
								.item(0).getTextContent();
                                                folderLocations = folderLocations.replaceAll("(\r\n|\n)", "<br />");
                                                eltfields.folderLocations = folderLocations;
                                        }
                                        
                                        
					if (eElement.getElementsByTagName("protected_user").item(0) != null) {
						eltfields.protected_user = eElement
								.getElementsByTagName("protected_user").item(0)
								.getTextContent();
					}
					if (eElement.getElementsByTagName("protected_comment")
							.item(0) != null) {
						eltfields.protected_comment = eElement
								.getElementsByTagName("protected_comment")
								.item(0).getTextContent();
					}

					eltfields.database_type = eElement
							.getElementsByTagName("database_type").item(0)
							.getTextContent();
					eltfields.schema = eElement.getElementsByTagName("schema")
							.item(0).getTextContent();
					eltfields.global_login = eElement
							.getElementsByTagName("global_login").item(0)
							.getTextContent();
					eltfields.global_password = eElement
							.getElementsByTagName("global_password").item(0)
							.getTextContent();
					eltfields.env_name = eElement
							.getElementsByTagName("environmentName").item(0)
							.getTextContent();
					eltfields.machine_name = eElement
							.getElementsByTagName("machineName").item(0)
							.getTextContent();
                                        // change end of lines with <br> tags
                                        String comments1= eElement
							.getElementsByTagName("comments").item(0)
							.getTextContent();
                                        comments1 = comments1.replaceAll("(\r\n|\n)", "<br />");
                                        eltfields.comments= comments1;       
                                        
					eltfields.url = eElement.getElementsByTagName("url")
							.item(0).getTextContent();
					eltfields.login = eElement.getElementsByTagName("login")
							.item(0).getTextContent();
					eltfields.password = eElement
							.getElementsByTagName("password").item(0)
							.getTextContent();
					eltfields.RDP = eElement.getElementsByTagName("RDP")
							.item(0).getTextContent();
					eltfields.admin_console = eElement
							.getElementsByTagName("admin_console").item(0)
							.getTextContent();
					eltfields.database_url = eElement
							.getElementsByTagName("database_url").item(0)
							.getTextContent();
					eltfields.version = eElement
							.getElementsByTagName("version").item(0)
							.getTextContent();
					eltfields.zone1_login = eElement
							.getElementsByTagName("zone1_login").item(0)
							.getTextContent();
					eltfields.zone1_password = eElement
							.getElementsByTagName("zone1_password").item(0)
							.getTextContent();
					eltfields.zone2_login = eElement
							.getElementsByTagName("zone2_login").item(0)
							.getTextContent();
					eltfields.zone2_password = eElement
							.getElementsByTagName("zone2_password").item(0)
							.getTextContent();

					/*
					 * xmlElement elt1 = new xmlElement("aa", "bb"); xmlElement
					 * elt2 = new xmlElement("cc", "dd"); xmlElement elt3 = new
					 * xmlElement("ee", "ff");
					 */
					runables[i] = new ProcessElement(eltfields,
							myHTMLGenerator, i); // Ihave traces there in the
													// constructor
                                    	// Creating the thread
					threads[i] = new Thread(runables[i], " Thread" + i); // not
																			// sure
																			// where
																			// i
																			// can
																			// add
																			// traces
					// starting thread
					threads[i].start(); // / I have traces there in the run

				}

			}
			}
			// checking termination
			boolean end = false;

			boolean terminatedStatuses[] = new boolean[size];
			for (int k = 0; k < size; k++) {
				terminatedStatuses[k] = false;
			}
			while (end == false) {
				end = true;
				// logger.info("checking termination");
				for (int j = 0; j < size; j++) {
					if (terminatedStatuses[j] == true) {
						continue; // skip that one
					}
					if (threads[j] != null) {
						if (threads[j].getState() == Thread.State.TERMINATED
								&& terminatedStatuses[j] == false) {
							logger.info("threads[" + j + "] terminated");
							terminatedStatuses[j] = true;
						} else {
							// logger.info("threads[" +j
							// +"] not terminated");
							end = false;
						}
					}
					try {
						sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			logger.info("all threads terminated");
			// FileWriter fileWriter = new
			// FileWriter("c:/temp/samplefile2.html");
			FileWriter fileWriter = new FileWriter(output);
			fileWriter.write(myHTMLGenerator.getString());
			fileWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
                
            }
                
            catch (Exception e) {
                    System.out.println("Failed to configure logging to file");
            }

	}

}

class HTMLGenerator {
	private Lock lock;
	// Lock to protect our counter
	private int count;
	// Integer to hold count
	private String htmlString;

	public HTMLGenerator(Lock myLock, String mystring) {
		this.lock = myLock;
		this.htmlString = mystring;
	}

	public final int getCount() {
		lock.lock();
		try {
			count++;
			return count;
		} finally {
			lock.unlock();
		}
	}

	public final String addToHTMLString(String mystring) {
		lock.lock();
		try {
			this.htmlString = this.htmlString + mystring;
			return this.htmlString;
		} finally {
			lock.unlock();
		}
	}

	public final String getString() {
		return this.htmlString;

	}

}

class result {
	boolean up;
	String vers;
	String timeStamp;
	String zoneVisitors;
	String globalVisitors;
	int nVisitors;

	public result(result res) // copy constructor
	{
		this.up = res.up;
		this.vers = res.vers;
		this.timeStamp = res.timeStamp;
	}

	public result(boolean up, String vers, String timeStamp) {
		this.up = up;
		this.vers = vers;
		this.timeStamp = timeStamp;
		this.zoneVisitors = "";
		this.globalVisitors = "";
		this.nVisitors = 0;
	}

	public result(boolean up, String vers, String timeStamp, int nVisitors,
			String zoneVisitors, String globalVisitors) {
		this.up = up;
		this.vers = new String(vers.toString());
		this.timeStamp = new String(timeStamp.toString());
		this.zoneVisitors = new String(zoneVisitors.toString());
		this.globalVisitors = new String(globalVisitors.toString());
		this.nVisitors = nVisitors;
	}

	public void populate(boolean up, String vers, String timeStamp) {
		this.up = up;
		this.vers = new String(vers.toString());
		this.timeStamp = new String(timeStamp.toString());

	}

	public boolean isup() {
		return this.up;
	}
}

class ProcessElement implements Runnable {
	int id;
	private HTMLGenerator myHTMLGenerator;
	/* static */protected Statement stmt, stmt2;
	/* static */protected ResultSet rs, rs1, rs2;
	/* static */Connection con, con2;
	String threadName;
        String priorityEnvironment;
        String customizedEnvironment;
        String customerDatabase;
        String folderLocations;
        String instructions;
	String environmentName;
	String database_type;
	String schema;
	String env_name;
	String machine_name;
        String comments;
	String url;
	String login;
	String password;
	String RDP;
	String admin_console;
	String database_url;
	String version;
	String global_login;
	String global_password;
	String zone1_login;
	String zone1_password;
	String zone2_login;
	String zone2_password;
	String protected_owner;
	String protected_user;
	String protected_comment;
	String inuse;

        class DBDataLoader
        {
            public boolean checkConnectedUsers(){
                //check if there are people logged into the system
                int countVisitors = 0;
                String sqlZones = "select b.username,  b.IP_ADDRESS, a.ZONE_ID, a.created from "
                                + schema
                                + ".local_Session_details a, "
                                + schema
                                + ".central_Session_details b where  a.CENTRAL_ID  =  b.CENTRAL_ID  and  a.ZONE_ID IS NOT NULL and a.ENDED IS NULL ORDER BY ZONE_ID";
                // String sqlGlobal =
                // "select USERNAME, IP_ADDRESS, CREATED from " + schema +
                // ".CENTRAL_SESSION_DETAILS where ENDED IS NULL ";
                try{
                    rs1 = stmt.executeQuery(sqlZones);
                    String ZoneVisitors = "", GlobalVisitors = "";
                    // check zones
                    while (rs1.next()) {
                            countVisitors++;
                            ZoneVisitors = ZoneVisitors + rs1.getString(1)
                                            + "&nbsp; " + rs1.getString(2) + "&nbsp; "
                                            + rs1.getString(3) + "&nbsp; "
                                            + rs1.getString(4) + "<br>";
                    }

                    returnvalue = new result(areweup, answers[0].vers
                                    + answers[1].vers + answers[2].vers, "",
                                    countVisitors, ZoneVisitors, "" /* GlobalVisitors */);

                }    
                catch (Exception e)
                {
                    //ignore
                }
                
                return true;};
            
            private boolean areweup=false;
            private String supervisorPassword="";
           public String getSupervisorPassword(){
                   return supervisorPassword;};
            public result getResult(){ return returnvalue;};
            private Connection con;
            private Statement stmt, stmt2;
            private result returnvalue;
            private boolean creatingJDBCObject;
            private String dbType;
            private result answers[];
	    private String sqlSession;	
            private boolean doFirstPass =  false;
            
            /*threadName, database_type,
				database_url, schema, global_login, global_password*/
            public DBDataLoader(String threadName, String dbType, String url,String schema, String user, String password )
            {
                
                //////$$//////////
                //////////////////
                /// initialize this one in case we crash
                returnvalue = new result(false, "&nbsp;  system down", "");
                Logger logger = Logger.getLogger("lockdemo");
                logger.info("inside DBBataLoader fot " + threadName);

                
                    this.dbType= dbType;
                    answers = new result[3];

                    logger.info(threadName + " url: " + url);
                    try {

                            if (dbType.equals("MsSQLServer")) {
                                    logger.info(threadName
                                                    + " **** do not Try \"MsSQL server...");

                                    // try{
                                    // Class.forName("net.sourceforge.jtds.jdbc.Driver");
                         //           logger.info(threadName
                           //                         + " **** Called Class.forName(\"MsSQL server...");

                            }
                            if (dbType.equals("DB2") || dbType.equals("DB2ForTI229")) {
                                    try {

                                            ClassLoader cl = ClassLoader.getSystemClassLoader();;
                                            Class.forName("com.ibm.db2.jcc.DB2Driver", false,cl);
                                            logger.info(threadName
                                                            + " **** Called Class.forName(\"ibm...");

                                    } catch (ClassNotFoundException e) {
                                           logger.severe(threadName
                                                                            + " **** Exceptopn in called Class.forName(\"ibm...");
                                            e.toString();
                                    }
                            }
                            if (dbType.equals("Oracle") || dbType.equals("OracleForTI25")) {

                                    try {
                             
                                            ClassLoader cl = ClassLoader.getSystemClassLoader();;
                                            Class.forName("oracle.jdbc.driver.OracleDriver",false,cl);
                                            logger.info(threadName
                                                            + " **** Called Class.forName(\"oracle...");

                                    } catch (ClassNotFoundException e) {
                                            logger.severe(threadName
                                                                            + " **** Exceptopn in called Class.forName(\"oracle...");
                                            e.toString();
                                    }

                            }
                            // Create the connection using the IBM Data Server Driver for JDBC

                            if (!user.isEmpty() && !password.isEmpty()
                                            && !url.isEmpty()) 
                            {
                                try {

                                            con = DriverManager.getConnection(url, user,
                                                            password);
                                            logger.info(threadName + " **** Got connection");
                                            creatingJDBCObject = true;
                                    } catch (Exception e) {
                                            logger.info(threadName
                                                            + " **** EXCEPTION on connection  ");
                                            e.toString();
                                            returnvalue = new result(false,
                                                            "&nbsp; &nbsp; system down", "");
                                    //	return returnvalue;

                                            // e.printStackTrace();
                                    }

                                    try {
                                            sleep(100);
                                    } catch (InterruptedException e) {
                                            e.printStackTrace();
                                    }    
                            } else {
                                    returnvalue = new result(false, "&nbsp;  system down", "");
                                    //return returnvalue;
                            }

                            /////////////////**//////////
                            // Create the Statement
                            
                            if (creatingJDBCObject) {
                                    try {
                                            stmt = con.createStatement();
                                            logger.info(threadName
                                                            + " **** Created JDBC Statement object");
                                    } catch (Exception e) {
                                            logger.info(threadName
                                                                            + " **** EXCEPTION in Created 1st JDBC Statement object");
                                            e.printStackTrace();

                                    }

                                    try {
                                            sleep(100);
                                    } catch (InterruptedException e) {
                                            e.printStackTrace();
                                    }

                            }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                
                
                
            public boolean waitForRefreshPeriod()
            {	
                Logger logger = Logger.getLogger("lockdemo");
                logger.info(threadName + " **** sleep for 70 s.");
                try {
                        // sleep(12000);
                        sleep(70000);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
                logger.info(threadName
                                + " **** end sleep for 70 s.");
                return true;
            }
            
             public boolean DoPass1()
            {
                Logger logger = Logger.getLogger("lockdemo");
               logger.info(threadName + " do pass 1");

                boolean success = false;
				sqlSession = "SELECT DEPLOYMENT_ID, SOFTWARE_VERSION, CONNECTION_STATUS, LAST_UPDATED from "
						+ schema + ".STS_DEPLOYMENT_SERVER";
                                // profile that is not security officers to se the issue
			        logger.info(threadName + " **** " + sqlSession);
				result rslt1 = new result(false, "", "");
				// initialize to down in case we exit of the loops with exceptions
				rslt1.vers = "";
				rslt1.timeStamp = "";
				rslt1.up = false;

				result rslt2 = new result(false, "", "");
				rslt2.vers = "";
				rslt2.timeStamp = "";
				rslt2.up = false;
				answers[0] = answers[1] = answers[2] = rslt1;

				answers[0].populate(rslt1.up, rslt1.vers, rslt1.timeStamp);
				answers[1].populate(rslt1.up, rslt1.vers, rslt1.timeStamp);
				answers[2].populate(rslt1.up, rslt1.vers, rslt1.timeStamp);

				if (dbType.equals("OracleForTI25")
						|| dbType.equals("DB2ForTI229")) {
					returnvalue = new result(true, "&nbsp; &nbsp; database up",
							"");
		//			return returnvalue;
				}
				try {
					rs1 = stmt.executeQuery(sqlSession);
					logger.info(threadName
							+ " **** Executed sql statement ");
					doFirstPass = true;

				} catch (Exception e) {
					logger.info(threadName
									+ " **** EXCEPTON executing sql statement 1st time ");
					// e.toString();
					e.toString();
				}

				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (doFirstPass) {
					try {
						while (rs1.next()) {

							if (rs1.getString(3).equals("CONNECTED")) {

								logger.info(threadName
										+ " **** something's up ");
								if (rs1.getString(1).equals("TI1")) {
									logger.info(threadName
											+ " **** TI1's up ");

									answers[0] = new result(true, "ZONE1: "
											+ rs1.getString(2) + "&nbsp;",
											rs1.getString(4));

								} else {
									if (rs1.getString(1).equals("TI2")) {
										logger.info(threadName
												+ " **** TI2's up ");

										answers[1] = new result(true, "ZONE2: "
												+ rs1.getString(2) + "&nbsp;",
												rs1.getString(4));

									} else // GLOBAL ???? and any 3rd zone ???
									{
										logger.info(threadName
												+ " **** GLOBAL's up ");

										answers[2] = new result(true,
												"GLOBAL: " + rs1.getString(2)
														+ "&nbsp;",
												rs1.getString(4));

									}
								}

							}
						}
					} catch (Exception e) {
						logger.info(threadName
								+ " **** EXCEPTION in 1st pass ");
						e.printStackTrace();

					}
                                        
                                        try {
                                                sleep(100);
                                        } catch (InterruptedException e) {
                                                e.printStackTrace();
                                        }

					if (answers[0].isup() || answers[1].isup()
							|| answers[2].isup()) {
						logger.info(threadName
								+ " **** finished first pass successfully");
                                                success = true;
					} else {
						logger.info(threadName
								+ " **** finished first pass UNsuccessfully");
						returnvalue = new result(false,
								"&nbsp; &nbsp; system down", "");
						//return returnvalue;
					}

                                }
                return  success;               
            }
            
            public boolean doPass2()
            {
                Logger logger = Logger.getLogger("lockdemo");
               logger.info(threadName + " do pass 2");
                boolean success = false;
					// ------------------ second pass ------------------
					if (answers[0].isup() || answers[1].isup()
							|| answers[2].isup()) {

						logger.info(threadName
								+ " **** Executing sql statement 2nd pass ");

					
                                                
                                                try {
                                                        sleep(100);
                                                } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                }
                                                try {
							rs2 = stmt.executeQuery(sqlSession);

							logger.info(threadName
									+ " **** Executed sql statement 2nd time ");
						

                                                try {
                                                        sleep(100);
                                                } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                }

						while (rs2.next()) {
			
							if (rs2.getString(1).equals("TI1")) { // no need to
																	// down on
																	// 1st pass
								logger.info(threadName
										+ " **** checking for TI1 (2)");

								if (!rs2.getString(4).equals(
										answers[0].timeStamp)
										&& answers[0].isup()) {
									answers[0] = new result(true,
											answers[0].vers,
											answers[0].timeStamp);
									logger.info(threadName
											+ " **** TI1's up ");
								} else {
									answers[0] = new result(false,
											answers[0].vers,
											answers[0].timeStamp);
									logger.info(threadName
											+ " **** TI1's down " + answers[0].timeStamp + " did not chnage");
								}
							} else {
								if (rs2.getString(1).equals("TI2")) {// no need
																		// pass
									logger.info(threadName
											+ " **** checking for TI2 (2)");

									if (!rs2.getString(4).equals(
											answers[1].timeStamp)
											&& answers[1].up == true) {
										answers[1] = new result(true,
												answers[1].vers,
												answers[1].timeStamp);
										logger.info(threadName
												+ " **** TI2's up (2)");
									} else {
										answers[1] = new result(false,
												answers[1].vers,
												answers[1].timeStamp);
										logger.info(threadName
												+ " **** TI2's down (2)");
									}
								} else // GLOBAL and any 3rd zone....
								{
									logger.info(threadName
											+ " **** checking for GLOBAL (2)");

									if (!rs2.getString(4).equals(
											answers[2].timeStamp)
											&& answers[2].up == true) {// no
																		// pass
										answers[2] = new result(true,
												answers[2].vers,
												answers[2].timeStamp);
										logger.info(threadName
												+ " **** GLOBAL's up (2)");
									} else {
										answers[2] = new result(false,
												answers[2].vers,
												answers[2].timeStamp);
										logger.info(threadName
												+ " **** GLOBAL's down (2)");
									}

								}
							}
							try {
								sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
                                            } catch (SQLException e) {
							logger.info(threadName
											+ " **** Exception executing sql statement 2nd time ");

							e.toString();
                                            }
					}
                                        else {
						logger.info(threadName
										+ " **** no need for  sql statement 2nt time, nothing up ");
						returnvalue = new result(false,
								"&nbsp; &nbsp; system down", "");
                                                // here system maybe up if Oracle
						//return returnvalue;
 
					}
                                        return true;
            }
            public boolean doCheckCOnnectedUsers()
            {
                Logger logger = Logger.getLogger("lockdemo");   
                 logger.info(threadName + " doCheckCOnnectedUsers");

			boolean areweup = (answers[0].isup() || answers[1].isup() || answers[2]
						.isup());
			if (areweup) {

					// ----------------- 3rd pass -- to check if there are
					// people logged into the system
					int countVisitors = 0;
					String sqlZones = "select b.username,  b.IP_ADDRESS, a.ZONE_ID, a.created from "
							+ schema
							+ ".local_Session_details a, "
							+ schema
							+ ".central_Session_details b where  a.CENTRAL_ID  =  b.CENTRAL_ID  and  a.ZONE_ID IS NOT NULL and a.ENDED IS NULL ORDER BY ZONE_ID";
					// String sqlGlobal =
					// "select USERNAME, IP_ADDRESS, CREATED from " + schema +
					// ".CENTRAL_SESSION_DETAILS where ENDED IS NULL ";
                                        
                                        try {
                                                sleep(100);
                                        } catch (InterruptedException e) {
                                                e.printStackTrace();
                                        }    
                                        try{
                                        rs1 = stmt.executeQuery(sqlZones);
					String ZoneVisitors = "", GlobalVisitors = "";
					// check zones
					
                                        while (rs1.next()) {
						countVisitors++;
						ZoneVisitors = ZoneVisitors + rs1.getString(1)
								+ "&nbsp; " + rs1.getString(2) + "&nbsp; "
								+ rs1.getString(3) + "&nbsp; "
								+ rs1.getString(4) + "<br>";
					}

					// ////////// return values

					logger.info(threadName + " ****  " + countVisitors + " users connected");
					returnvalue = new result(areweup, answers[0].vers
							+ answers[1].vers + answers[2].vers, "",
							countVisitors, ZoneVisitors, "" /* GlobalVisitors */);

				} 
                                catch (SQLException e) {
							logger.info(threadName
											+ " **** Exception executing sql statement to check connected users ");

							e.toString();
				}
                                        } else {
				// HERE WE DO NOT SAYS SYSTEM DOWN IF 2ND PASS SAW THE SAME TIMESTAMP AND ORACLE
                                    if (dbType.equals("Oracle"))
                                    { 
					returnvalue = new result(areweup,
							 answers[0].vers
									+ answers[1].vers + answers[2].vers, "");
                                    }
                                    else
                                    {
                                        returnvalue = new result(areweup,
							"&nbsp; &nbsp; system down <br>" + answers[0].vers
									+ answers[1].vers + answers[2].vers, "");
                                    
                                    }
				}
                            return true;    
			}

            
            
            
             public void checkSupervisorPassword()
            {
                Logger logger = Logger.getLogger("lockdemo");
                 logger.info(threadName + " getSupervisorPassword");
                 String sqlPassword = "select PASSWORD from "
                                                    + schema
                                                    + ".SS_USER "
                                                    + " WHERE USERNAME=\'SUPERVISOR\'";
                    try{
                            rs1 = stmt.executeQuery(sqlPassword);
                            while (rs1.next()) {
                                    String pwd =  rs1.getString(1);
                                    
                                    switch (pwd)
                                            {
                                    case "!A!Lyprk456M21D8iXbKF/CXbyROxA=":
                                           supervisorPassword="SUPERVISOR1";
                                           break;
                                    case "!A!l2QL/skJDU5RwGpikCbp+Crc3Hw":  
                                           supervisorPassword="SUPERVISOR2";
                                           break;
                                    case  "!A!EJxW8susFgscONF01wz/CPgGREU=":
                                            supervisorPassword="SUPERVISOR3";
                                            break;
                                    case  "!A!hRUd++Fzhwe8ECaAzpcqtkz6X/Q=":
                                        supervisorPassword="SUPERVISOR4";
                                        break;
                                    case "!A!fyxzTcaQRD3Y+r3lN6ll2UG+W7s='":
                                        supervisorPassword="SUPERVISOR5";
                                        break;
                                    default:
                                        supervisorPassword="NO IDEA";
                                        break;
                                    }
                                     
              
                                    logger.info(threadName + " password: " + supervisorPassword);
                            }
                    }

                    catch (SQLException e) {
                                           logger.severe(threadName
                                                                            + " **** Exception executing sql to check the password ");

                                            e.toString();
                }
                
            }
             
             
              public void resetSupervisorPassword()
            {
                int  rs1;
                Logger logger = Logger.getLogger("lockdemo");
                 logger.info(threadName + " resetSupervisorPassword");
                 //UPDATE TIGLOBAL28.SS_USER SET PASSWORD='Lyprk456M21D8iXbKF/CXbyROxA=', ACCOUNT_ENABLED='1', PASSWORD_HISTORY='',  PASSWORD_RESET='0' WHERE USERNAME='SUPERVISOR';

                 String sqlPassword = "UPDATE " + schema 
                                                    + ".SS_USER "
                                                    + "SET PASSWORD=\'Lyprk456M21D8iXbKF/CXbyROxA=\', ACCOUNT_ENABLED=\'1\', PASSWORD_HISTORY=\'\',  PASSWORD_RESET=\'0\'"    
                                                    + " WHERE USERNAME=\'SUPERVISOR\'";
                    try{
                            rs1 = stmt.executeUpdate(sqlPassword);
                            
                                    logger.info(threadName + " supervisor password reset successfully");
                    }
                    

                    catch (SQLException e) {
                                            logger.severe(threadName
                                                                            + " **** Exception executing sql to reset the password ");

                                            e.toString();
                }
                
            } 
             
             
            
                //////////$//////////
                /////////////////////
                
            }
            
        
        
	public ProcessElement(ElementFields elements, HTMLGenerator counter,
			int myId) {
                Logger logger = Logger.getLogger("lockdemo");
                id = myId;
                
		priorityEnvironment = elements.priorityEnvironment;
		customizedEnvironment = elements.customizedEnvironment;
		customerDatabase = elements.customerDatabase;
                instructions = elements.instructions;
                folderLocations = elements.folderLocations;
                
		environmentName = elements.environmentName;
		database_url = elements.database_url; // we make sure we allocate memory
												// for this here as we want our
												// own copy of this

		logger.info("DB url to pass to Thread" + id + ": "
				+ database_url);
		database_type = elements.database_type; // we make sure we allocate
												// memory for this here as we
												// want our own copy of this
		schema = elements.schema;
		env_name = elements.env_name;
		machine_name = elements.machine_name;
                comments = elements.comments;
		url = elements.url;
		// threadName = Thread.currentThread().getName();

		login = elements.login;
		password = elements.password;
		RDP = elements.RDP;
		admin_console = elements.admin_console;
		version = elements.version;
		global_login = elements.global_login;
		global_password = elements.global_password;
		zone1_login = elements.zone1_login;
		zone1_password = elements.zone1_password;
		zone2_login = elements.zone2_login;
		zone2_password = elements.zone2_password; // because at a higher level
													// thsi elt passed to us
													// keeps being overwriten
		myHTMLGenerator = counter;
		protected_owner = elements.protected_owner;
		protected_user = elements.protected_user;
		protected_comment = elements.protected_comment;
		// store parameter for later user
	}

	public void run() {
		Logger logger = Logger.getLogger("lockdemo");
                String sout;
		// get the tags to use ... done in constructor
		// do some connection (twice) and prepare some HTML string

		threadName = Thread.currentThread().getName();
                logger.info("run Thread" + threadName);
		
                try {
			sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
                
                
                //Create the HTML table and define the columns
		sout = "<table style=\"width:100%\">";
		sout = sout + "<colgroup>";
                                // the star for priority envs
		sout = sout   + "<col  width=\"2%\" style=\"background-color:white\"  >";
                            //// the environment title
                sout = sout   + "<col  width=\"15%\" style=\"background-color:lightgrey\"  >";
                            /// environment details
		sout = sout   + "<col  width=\"25%\"  style=\"background-color:white\"  >";
                           
                            // system free or busy
		sout = sout   + "<col  width=\"4%\"  style=\"background-color:white\"  >";
                 // number of visitors
		sout = sout   + "<col  width=\"10%\"  style=\"background-color:white\"  >";
		sout = sout   + "</colgroup>";
                // instructions
                sout = sout   + "<col  width=\"25%\"  style=\"background-color:white\"  >";
                            // folder locations
                sout = sout   + "<col  width=\"19%\"  style=\"background-color:white\"  >";
                           
                // the first column: with or without PRIO  icon 
                
                if (priorityEnvironment != null) {
                    sout = sout + "<th><img src=\"prio.png\" alt=\"prio\" width=\"20\" height=\"20\"> </th>";
                }
                else
                {
                    sout = sout + "<th></th>";
                }
                
                
		try {
			sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
                
                /////////////////$$$//////////////
                /*		result retval = ConnectAndGetVersion(threadName, database_type,
				database_url, schema, global_login, global_password);
                */
                //////////////////////////////////
                ///  conect to the atabase and get some information back
                DBDataLoader dbl = new DBDataLoader(threadName, database_type,
				database_url, schema, global_login, global_password);
                logger.info(threadName + " DBDataLoader constructed");
                    
                if (dbl.DoPass1())
                {

                    // now wait 60 s. and do it again to check if the timestamps have changed
                    // we do that only if the fisrt time we got answers
                    dbl.waitForRefreshPeriod();
                    dbl.doPass2();
                    dbl.doCheckCOnnectedUsers();
                    dbl.checkSupervisorPassword();
                }
                 result retval=dbl.getResult();
                 String pwd= dbl.getSupervisorPassword();
                if( pwd.equals("NO IDEA"))
                {//rest
                    dbl.resetSupervisorPassword();
                }
                else {   password = pwd;  }
                
             
		if (retval.up) {
			sout = sout
					+ "<th align=\"left\" style=\"background-color:lightgreen\" >"
					+ environmentName + "</th>";
		} else {
			sout = sout + "<th align=\"left\" >" + environmentName + "</th>";
		}

		sout = sout + "<th>";
		sout = sout + "<details align=\"left\"  >";
		sout = sout + "<summary>" + retval.vers + "</summary>";
                sout = sout + "<br>customer database: " + customerDatabase;
                sout = sout + "<br>customized: " + customizedEnvironment;
                sout = sout + "<br>" + comments + "<br><br>url : <a href=\"" + url + "\">" + url + "</a>";

		if (protected_owner == null) // no need to keep it protected
		{
			sout = sout + "<br>login : "
					+ "<font face=\"calibri\" weigt=\"normal\">" + login;
			sout = sout + "<br>password : "
					+ "<font face=\"calibri\" weigt=\"normal\">" + password;
			sout = sout + "<br><br>RDP : "
					+ "<font face=\"calibri\" weigt=\"normal\">" + RDP;
			sout = sout + "<br>admin_console : <a href=\"" + admin_console
					+ "\">" + admin_console + "</a>";
			sout = sout + "<br><br>database_type : "
					+ "<font face=\"calibri\" weigt=\"normal\">"
					+ database_type;
			sout = sout + "<br>database_url : "
					+ "<font face=\"calibri\" weigt=\"normal\">" + database_url;
			sout = sout + "<br><br>global_login : "
					+ "<font face=\"calibri\" weigt=\"normal\">" + global_login;
			sout = sout + "<br>global_password : "
					+ "<font face=\"calibri\" weigt=\"normal\">"
					+ global_password;
			sout = sout + "<br>zone1_login : "
					+ "<font face=\"calibri\" weigt=\"normal\">" + zone1_login;
			sout = sout + "<br>zone1_password : "
					+ "<font face=\"calibri\" weigt=\"normal\">"
					+ zone1_password;
			sout = sout + "<br>zone2_login : "
					+ "<font face=\"calibri\" weigt=\"normal\">" + zone2_login;
			sout = sout + "<br>zone2_password : "
					+ "<font face=\"calibri\" weigt=\"normal\">"
					+ zone2_password;
		} else {
			sout = sout + "<br>Env owned by  " + protected_owner
					+ " <font face=\"calibri\" weigt=\"normal\">";

			if (!protected_user.isEmpty()) {
				sout = sout + "<br>use user: " + protected_user
						+ " <font face=\"calibri\" weigt=\"normal\">";

			}
			if (!protected_comment.isEmpty()) {
				sout = sout + "<br>" + protected_comment
						+ " <font face=\"calibri\" weigt=\"normal\">";

			}
		}
		sout = sout + "</details>";
		sout = sout + "</th>";
		
                                
      
                // // visitors/////////////////
		if (retval.nVisitors > 0) 
                {
			if (retval.nVisitors < 3) {
				sout = sout
						+ "<th align=\"left\" style=\"background-color:yellow\" >"
						+ " &nbsp; busy &nbsp;    " + "</th>";
				
			}
			if (retval.nVisitors >= 3 && retval.nVisitors <= 7) {
				sout = sout
						+ "<th align=\"left\" style=\"background-color:orange\" >"
						+ " &nbsp; busy &nbsp;    " + "</th>";
				
			}
			if (retval.nVisitors > 7) {
				sout = sout
						+ "<th align=\"left\" style=\"background-color:red\" >"
						+ " &nbsp; busy &nbsp;    " + "</th>";
				
			}
                        // less than 3 visitors, system free
                        

                        // the users themselves
                        sout = sout + "<th>";
			sout = sout + "<details align=\"left\"  >";
			sout = sout + "<summary>" + "users logged in"
					+ "</summary>";
			sout = sout  + retval.zoneVisitors + "</a>";
			sout = sout + "<br>" + retval.globalVisitors + "</a>";
                        
		} else {  //(only if the system is up and is not older than 2.8)
			if (retval.up && (!retval.vers.equals("&nbsp; &nbsp; database up"))) {
				sout = sout
						+ "<th align=\"left\" style=\"background-color:lightgreen\" >"
						+ " &nbsp; free &nbsp;    " + "</th>";
				sout = sout + "<th>";
				//sout = sout + "<details align=\"left\"  >";
				//sout = sout + "<summary>" + "&nbsp;" + "</summary>";
				//sout = sout + "<br>" + "&nbsp;" + "</a>";
			} else {
				sout = sout + "<th align=\"left\"  >" + " &nbsp;  &nbsp;    "
						+ "</th>";
				sout = sout + "<th>";
				//sout = sout + "<details align=\"left\"  >";
				//sout = sout + "<summary>" + "&nbsp;" + "</summary>";
				//sout = sout + "<br>" + "&nbsp;" + "</a>";
			}

		}
                
		sout = sout + "</th>";
                          // add the instructions column ONLY IF we have instructions to give
                if (instructions != null)
                {
                        sout = sout + "<th><details align=\"left\"  >";
			sout = sout + "<summary>" + "instructions"
					+ "</summary>";
			sout = sout  + instructions + " </a></th>";
                }	    
               else // to keep the columns in place
                {
                    sout = sout + "<th></th>";
                }

               // add the folder locations column ONLY IF we have locations to give
               if(folderLocations != null)
               {
                        sout = sout + "<th><details align=\"left\"  >";
			sout = sout + "<summary>" + "folder locations"
					+ "</summary>";
			sout = sout  + folderLocations +"</a></th>";
			//sout = sout + "<br> sample tetxt </a>";
               }
               else // to keep the columns in place
                {
                    sout = sout + "<th></th>";
                }


		// ///////////////////////////
        
                        
                        
                        
                        
                
                // close the HTML table
                sout = sout + "</table >";
		// add the text to the HTML string of the paage (on going building of
		// the string through all the threads)
		myHTMLGenerator.addToHTMLString(sout);
		// DEBUG TRACE ONLY
		// logger.info(myHTMLGenerator.getString());
		logger.info(threadName + " added the env HTML to HTMLGenerator");

	}

        
}

class ElementFields {

	public void ElementFields() {
		priorityEnvironment = "";
                customizedEnvironment = "";
                customerDatabase = "";
                instructions="";
                folderLocations="";
                environmentName = "";
		database_type = "";
		schema = "";
		env_name = "";
		machine_name = "";
                comments = "";
		url = "";
		login = "";
		password = "";
		RDP = "";
		admin_console = "";
		database_url = "";
		version = "";
		global_login = "";
		global_password = "";
		zone1_login = "";
		zone1_password = "";
		zone2_login = "";
		zone2_password = "";
		protected_owner = "";
		protected_user = "";
		protected_comment = "";
		inuse =""; 
	}
        String priorityEnvironment;
        String customizedEnvironment;
        String customerDatabase;
        String instructions;
        String folderLocations;
	String environmentName;
	String database_type;
	String schema;
	String env_name;
	String machine_name;
        String comments;
	String url;
	String login;
	String password;
	String RDP;
	String admin_console;
	String database_url;
	String version;
	String global_login;
	String global_password;
	String zone1_login;
	String zone1_password;
	String zone2_login;
	String zone2_password;
	String protected_owner;
	String protected_user;
	String protected_comment;
	String inuse;
}
