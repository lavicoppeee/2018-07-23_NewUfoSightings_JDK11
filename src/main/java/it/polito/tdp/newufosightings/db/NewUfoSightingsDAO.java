package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Arco;
import it.polito.tdp.newufosightings.model.Event;
import it.polito.tdp.newufosightings.model.Event.EventType;
import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;

public class NewUfoSightingsDAO {

	public List<Sighting> loadAllSightings() {
		String sql = "SELECT * FROM sighting";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public List<State> loadAllStates() {
		String sql = "SELECT * FROM state";
		List<State> result = new ArrayList<State>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				result.add(state);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<State> getStati(int anno, String shape, Map<String, State> idMap) {
		String sql="SELECT st.`Area`,st.`Capital`,st.`id`, st.`Lat`, st.`Lng`, st.`Name`, st.`Neighbors`, st.`Population` " + 
				"FROM sighting as s, state as st " + 
				"WHERE YEAR(s.`datetime`)=? and s.shape=? and s.state=st.`id` " + 
				"GROUP BY st.id " + 
				"ORDER BY st.id ASC ";
		List<State> result = new ArrayList<State>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setString(2, shape);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(!idMap.containsKey(rs.getString("id"))) {
				State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				idMap.put(state.getId(), state);
				result.add(state);
				}
			}

			conn.close();
		    return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");

		}
		
	}

	public List<String> getShape(int anno) {
		String sql="SELECT s.`shape` as s " + 
				"FROM sighting as s " + 
				"WHERE YEAR(s.`datetime`)= ? " + 
				"GROUP BY s " + 
				"ORDER BY s ASC ";
		List<String> result=new ArrayList<>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				result.add(rs.getString("s"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Arco> getVicino(int year, String shape, Map<String, State> idMap) {
		String sql = "SELECT s1.id AS id1, s2.id AS id2, COUNT(DISTINCT v1.id)+COUNT(DISTINCT v2.id) AS peso " + 
				"FROM state AS s1, state AS s2, neighbor AS n, sighting AS v1, sighting AS v2 " + 
				"WHERE n.state1 = s1.id AND n.state2 = s2.id AND " + 
				"YEAR(v1.datetime) = ? AND YEAR(v2.datetime) = ? AND " + 
				"v1.shape = ? AND v2.shape = ? AND s1.id = v1.state AND s2.id = v2.state " + 
				"GROUP BY s1.id, s2.id ";
		List<Arco> list = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			st.setInt(2, year);
			st.setString(3, shape);
			st.setString(4, shape);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				State s1 = idMap.get(res.getString("id1"));
				State s2 = idMap.get(res.getString("id2"));
				list.add(new Arco(s1, s2, res.getDouble("peso")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}
	
	
	//getALLtheEVENTs
	
	public List<Event> getAllEvent(int year, String shape, Map<String,State> idMap){
		String sql="SELECT DATETIME, state " + 
				"FROM sighting " + 
				"WHERE YEAR(DATETIME) = ? AND shape = ? " +
				"ORDER BY DATETIME";
			
	    List<Event> result=new ArrayList<>();
	    try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			st.setString(2, shape);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				if(idMap.containsKey(res.getString("state").toUpperCase())) {
					State state = idMap.get(res.getString("state").toUpperCase());
					result.add(new Event(EventType.ALLERTA, state, res.getTimestamp("datetime").toLocalDateTime()));
				}
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return result;
	}

}

