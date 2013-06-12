package com.geeksong.agricolascorer.mapper;

import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.geeksong.agricolascorer.managers.AllCreaturesScoreManager;
import com.geeksong.agricolascorer.model.AllCreaturesScore;
import com.geeksong.agricolascorer.model.Game;
import com.geeksong.agricolascorer.model.GameType;
import com.geeksong.agricolascorer.model.Player;
import com.geeksong.agricolascorer.model.Score;

public class AllCreaturesScoreMapper extends BaseScoreMapper {
	public ContentValues toDatabase(Score baseScore, long gameId) {
		ContentValues values = new ContentValues();
		AllCreaturesScore score = (AllCreaturesScore) baseScore;
		
		values.put(Database.KEY_SHEEP_COUNT, score.getSheepScore());
		values.put(Database.KEY_WILD_BOAR_COUNT, score.getWildBoarScore());
		values.put(Database.KEY_CATTLE_COUNT, score.getCattleScore());
		values.put(Database.KEY_HORSE_COUNT, score.getHorseScore());
		values.put(Database.KEY_FULL_EXPANSION_COUNT, score.getFullExpansionScore() / 4);
		values.put(Database.KEY_BUILDING_POINTS, score.getBuildingScore());
		values.put(Database.KEY_PLAYERID, score.getPlayer().getId());
		values.put(Database.KEY_GAMEID, gameId);
		
		return values;
	}

	public Score toScore(Cursor cursor, Player player) {
    	AllCreaturesScore score = new AllCreaturesScore(player);
    	score.setId(cursor.getInt(5));
    	score.setSheepScore(AllCreaturesScoreManager.getScoreForSheep(cursor.getInt(6)));
    	score.setSheepBonusScore(AllCreaturesScoreManager.getBonusScoreForSheep(score.getSheepScore()));
    	score.setWildBoarScore(AllCreaturesScoreManager.getScoreForWildBoar(cursor.getInt(7)));
    	score.setWildBoarBonusScore(AllCreaturesScoreManager.getBonusScoreForWildBoar(score.getWildBoarScore()));
    	score.setCattleScore(AllCreaturesScoreManager.getScoreForCattle(cursor.getInt(8)));
    	score.setCattleBonusScore(AllCreaturesScoreManager.getBonusScoreForCattle(score.getCattleScore()));
    	score.setHorseScore(AllCreaturesScoreManager.getScoreForHorse(cursor.getInt(9)));
    	score.setHorseBonusScore(AllCreaturesScoreManager.getBonusScoreForHorse(score.getHorseScore()));    	
    	score.setFullExpansionScore(AllCreaturesScoreManager.getScoreForFullExpansion(cursor.getInt(10)));
    	score.setBuildingScore(AllCreaturesScoreManager.getScoreForBuildings(cursor.getInt(11)));
    	    	
    	return score;
	}

	@Override
	protected Cursor getGamesCursor(Database db) {
    	String selectQuery = String.format(Locale.US, "SELECT " +
    			"games.%s as _id, games.%s, games.%s, players.%s, players.%s, " +
    			"scores.%s, scores.%s, scores.%s, scores.%s, scores.%s, scores.%s, scores.%s " +
    			"FROM %s as scores " +
    			"JOIN %s as players on players.%s = scores.%s " +
    			"JOIN %s as games on games.%s = scores.%s " +
    			"WHERE games.%s=%d ", 
    			Database.KEY_ID, Database.KEY_DATE, Database.KEY_GAME_TYPE, Database.KEY_NAME, Database.KEY_ID, 
    			Database.KEY_ID, Database.KEY_SHEEP_COUNT, Database.KEY_WILD_BOAR_COUNT, Database.KEY_CATTLE_COUNT, Database.KEY_HORSE_COUNT, Database.KEY_FULL_EXPANSION_COUNT, Database.KEY_BUILDING_POINTS,
    			Database.TABLE_ALL_CREATURES_SCORES,
    			Database.TABLE_RECENTPLAYERS, Database.KEY_ID, Database.KEY_PLAYERID,
    			Database.TABLE_GAMES, Database.KEY_ID, Database.KEY_GAMEID,
    			Database.KEY_GAME_TYPE, GameType.AllCreatures.ordinal());
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        return sqlDb.rawQuery(selectQuery, null);
	}

	public void deleteGame(Database db, Game game) {
    	SQLiteDatabase sqlDb = db.getWritableDatabase();
    	String gameId = Integer.toString(game.getId());
    	
    	sqlDb.delete(Database.TABLE_ALL_CREATURES_SCORES, Database.KEY_GAMEID + " = ?", new String[] { gameId });    	
    	sqlDb.delete(Database.TABLE_GAMES, Database.KEY_ID + " = ?", new String[] { gameId });
	}
}
