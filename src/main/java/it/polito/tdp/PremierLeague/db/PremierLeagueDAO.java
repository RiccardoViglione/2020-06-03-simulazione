package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer,Player>idMap){
		String sql = "SELECT * FROM Players";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
if(!idMap.containsKey(res.getInt("PlayerID"))) {
				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));

idMap.put(player.getPlayerID(), player);
}
			
			}
			conn.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Player> getVertici(double x,Map<Integer,Player>idMap){
		String sql = "select p.`PlayerID` "
				+ "from `Players`p,Actions a  "
				+ "where a.`PlayerID`=p.`PlayerID` "
				+ "group by p.`PlayerID` "
				+ "having avg(a.`Goals`)>?";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, x);
			ResultSet res = st.executeQuery();
			while (res.next()) {
result.add(idMap.get(res.getInt("PlayerID")));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Adiacenza> getArchi(Map<Integer,Player>idMap){
		String sql = "select distinct  p.`PlayerID` as id1,p2.PlayerId as id2,(sum(a.`TimePlayed`)-(sum(a2.`TimePlayed`)))as peso "
				+ "from `Players`p,Actions a,Players p2,Actions a2 "
				+ "where a.`PlayerID`=p.`PlayerID` and a2.`PlayerID`=p2.`PlayerID` and a2.`MatchID`=a.`MatchID` "
				+ "and a.`TeamID`!=a2.`TeamID` and a.`Starts`=1 and a2.`Starts`=1 AND a.PlayerID > a2.PlayerID  "
				+ "group by p.`PlayerID`,p2.PlayerId " 
				+ "having peso!=0 ";
				
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet res = st.executeQuery();
			while (res.next()) {
result.add(new Adiacenza(idMap.get(res.getInt("id1")),idMap.get(res.getInt("id2")),res.getInt("peso")));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
