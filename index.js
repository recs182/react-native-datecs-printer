
import { NativeModules } from 'react-native';

const { RNDatecsPrinter } = NativeModules;

export default {
	printTaggedText: function(){
		RNDatecsPrinter.printTaggedText(),
	}
};
