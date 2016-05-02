# Require any additional compass plugins here.
add_import_path "bower_components/foundation/scss"
add_import_path "bower_components/slick.js/slick"
#add_import_path "bower_components/fontawesome/scss"

require 'sass-globbing'
# require 'css_splitter'

# Set this to the root of your project when deployed:
http_path = "/"
css_dir = "../../main/content/jcr_root/etc/designs/gsusa/clientlibs/css"
sass_dir = "custom/scss"
images_dir = "../../main/content/jcr_root/etc/designs/gsusa/clientlibs/images"
javascripts_dir = "custom/js"
fonts_dir = "bower_components/slick.js/slick/fonts"


# on_stylesheet_saved do |path|
#   CssSplitter.split(path) unless path[/\d+$/]
# end

# sourcemap = false

# You can select your preferred output style here (can be overridden via the command line):
# output_style = :expanded or :nested or :compact or :compressed

# To enable relative paths to assets via compass helper functions. Uncomment:
relative_assets = true

# To disable debugging comments that display the original location of your selectors. Uncomment:
# line_comments = false

# If you prefer the indented syntax, you might want to regenerate this
# project again passing --syntax sass, or you can uncomment this:
# preferred_syntax = :sass
# and then run:
# sass-convert -R --from scss --to sass sass scss && rm -rf sass && mv scss sass