# MiniMinesweeper
 [Google Play Store](https://play.google.com/store/apps/details?id=com.timmyho.miniminesweeper)

This app is based on the Windows game called Minesweeper. It allows the user to click the square and hold (longClick) to flag it. The game ends when all the non-mine squares are clicked (win) or when a mine square is clicked (lose). There also is a Best Times list for keeping track of all winning times.

Though I have had expereince writing Windows Store apps (XAML/C#), this is the first Android app I've written. The biggest difference If found was how the UI layout/updating is handled. Note that I tried to make the code and structure as clean as possible, and am overall very happy with the app.

| Feature                                           | Android element/API                                    |
| ------------------------------------------------- | ------------------------------------------------------ |
| UI layout                                         | LinearLayout, layout_weight, gravity                   |
| Touch events                                      | OnClick, OnLongClickListener, OnItemClickListener      |
| Best Time name entry, times clearing confirmation | (extend) DialogFragment                                |
| Minesweeper image squares, Best Time item         | GridView, ListView, (extend) BaseAdapter               |
| Sending and receiving data between activitities   | Intent, extras                                         |
| Activity lifecycle management                     | onPause, onResume                                      |
| Saving state (orientation change)                 | onSaveInstanceState/onRestoreInstanceState, Parcelable |
| Persistent list of times                          | SQLiteDatabase, SQLiteOpenHelper                       |
| In-game timer                                     | Handler, Runnable                                      |

## Possible other features
- Multiple language support (localization)
- Sharing TimesList across activities (custom Fragment)
- Difficulty
- Hint system
- More precise timer
