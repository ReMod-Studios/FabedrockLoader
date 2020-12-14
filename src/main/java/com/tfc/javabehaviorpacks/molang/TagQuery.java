//package com.tfc.javabehaviorpacks.molang;
//
//import com.eliotlash.mclib.math.IValue;
//import com.eliotlash.mclib.math.functions.Function;
//import net.minecraft.block.BlockState;
//import net.minecraft.tag.BlockTags;
//
//import java.util.ArrayList;
//
//public class TagQuery extends Function {
//	public static BlockState currentBlock = null;
//
//	public TagQuery(IValue[] values, String name) throws Exception {
//		super(values, name);
//	}
//
//	@Override
//	public double getArg(int index) {
//		return super.getArg(index);
//	}
//
//	@Override
//	public String toString() {
//		return super.toString();
//	}
//
//	@Override
//	public String getName() {
//		return super.getName();
//	}
//
//	@Override
//	public int getRequiredArguments() {
//		return 0;
//	}
//
//	@Override
//	public double get() {
//		int num = 0;
//		try {
//			while (true) {
//				if (currentBlock.isIn(BlockTags.getTagGroup().getTag(getArg(num)))) {
//					return 1;
//				}
//				num++;
//			}
//		} catch (Throwable ignored) {
//		}
//		return 0;
//	}
//}
