import React, { Component } from 'react';
import { AppRegistry, StyleSheet, Text, View } from 'react-native';

export default class DatecsPrinterExample extends Component {
	render() {
		return (
			<View style={styles.container}>
				<Text style={styles.android}>Only for Android :/</Text>
			</View>
		);
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		justifyContent: 'center',
		alignItems: 'center',
		backgroundColor: '#F5FCFF',
	},
	android: {
		fontSize: 20,
		textAlign: 'center',
		margin: 10,
	},
});

AppRegistry.registerComponent('DatecsPrinterExample', () => DatecsPrinterExample);
