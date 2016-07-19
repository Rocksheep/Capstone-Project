package nl.codesheep.android.pagesforreddit.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface SubRedditsTable {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull String NAME = "name";

}
