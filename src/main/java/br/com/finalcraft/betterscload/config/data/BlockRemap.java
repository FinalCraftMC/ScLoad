package br.com.finalcraft.betterscload.config.data;

import com.sk89q.worldedit.blocks.BaseBlock;

public class BlockRemap {

    int itemIDOrigin;
    int itemMetavalueOrigin;

    int itemIDTarget;
    int itemMetavalueTarget;

    public BlockRemap(String aLine) throws Exception{
        String[] splited = aLine.split(" - ");

        if (splited[0].contains(":")){
            String[] secondSplit = splited[0].split(":");
            itemIDOrigin = Integer.parseInt(secondSplit[0]);
            itemMetavalueOrigin = Integer.parseInt(secondSplit[1]);
        }else {
            itemIDOrigin = Integer.parseInt(splited[0]);
            itemMetavalueOrigin = -1;
        }

        if (splited[1].contains(":")){
            String[] secondSplit = splited[1].split(":");
            itemIDTarget = Integer.parseInt(secondSplit[0]);
            itemMetavalueTarget = Integer.parseInt(secondSplit[1]);
        }else {
            itemIDTarget = Integer.parseInt(splited[0]);
            itemMetavalueTarget = -1;
        }
    }

    public void applyRemap(BaseBlock baseBlock){
        baseBlock.setType(this.itemIDTarget);
        if (this.itemMetavalueTarget != -1){
            baseBlock.setData(this.itemMetavalueTarget);
        }
    }

    public String getOriginIdentifier(){
        return itemIDOrigin + ":" + itemMetavalueOrigin;
    }

    public String getTargetIdentifier(){
        return itemIDTarget + ":" + itemMetavalueTarget;
    }
}
