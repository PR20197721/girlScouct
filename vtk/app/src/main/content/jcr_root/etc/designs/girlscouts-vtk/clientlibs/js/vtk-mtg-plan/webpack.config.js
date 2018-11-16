var webpack = require("webpack");

module.exports = function(env){


    //Apply enviroment
    var plugins = [
        new webpack.DefinePlugin({
        "___ENV___": JSON.stringify({
            "env":env,
            "host":'http://localhost:4503',
            "db_host":"http://localhost:3000",
            "modal":"meetings",
            "meeting":"701G0000000uQzUIAU"
        })
    })]

    
    return {
        entry: "./vtk-mtg.tsx",
        output: {
            filename: "vtk-mtg-plan.js",
            path: __dirname + "./../"
        },

        // Enable sourcemaps for debugging webpack's output.
        devtool: "source-map",

        resolve: {
            // Add '.ts' and '.tsx' as resolvable extensions.
            extensions: [".ts", ".tsx", ".js", ".json"]
        },

        module: {
            rules: [
                // All files with a '.ts' or '.tsx' extension will be handled by 'awesome-typescript-loader'.
                { test: /\.tsx?$/, loader: "awesome-typescript-loader" },

                // All output '.js' files will have any sourcemaps re-processed by 'source-map-loader'.
                { enforce: "pre", test: /\.js$/, loader: "source-map-loader" },

                {
                test: /\.scss$/,
                use: [{
                    loader: "style-loader" // creates style nodes from JS strings
                }, {
                    loader: "css-loader" // translates CSS into CommonJS
                }, {
                    loader: "sass-loader" // compiles Sass to CSS
                }]
            }]
        },


        plugins: plugins,

        // When importing a module whose path matches one of the following, just
        // assume a corresponding global variable exists and use that instead.
        // This is important because it allows us to avoid bundling all of our
        // dependencies, which allows browsers to cache those libraries between builds.
        externals: {
            "react": "React",
            "react-dom": "ReactDOM"
        },
    }
};