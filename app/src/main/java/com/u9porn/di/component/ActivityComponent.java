package com.u9porn.di.component;

import com.u9porn.ui.axgle.AxgleFragment;
import com.u9porn.ui.basemain.BaseMainFragment;
import com.u9porn.ui.download.DownloadingFragment;
import com.u9porn.ui.download.FinishedFragment;
import com.u9porn.ui.images.huaban.HuaBanFragment;
import com.u9porn.ui.images.meizitu.MeiZiTuFragment;
import com.u9porn.ui.images.mm99.Mm99Fragment;
import com.u9porn.ui.mine.MineFragment;
import com.u9porn.ui.pav.PavFragment;
import com.u9porn.ui.porn9forum.Forum9IndexFragment;
import com.u9porn.ui.porn9forum.ForumFragment;
import com.u9porn.ui.porn9video.author.AuthorFragment;
import com.u9porn.ui.porn9video.comment.CommentFragment;
import com.u9porn.ui.porn9video.favorite.FavoriteFragment;
import com.u9porn.ui.porn9video.index.IndexFragment;
import com.u9porn.ui.porn9video.videolist.VideoListFragment;

/**
 * @author flymegoc
 * @date 2018/2/4
 */

public interface ActivityComponent {

    void inject(VideoListFragment videoListFragment);

    void inject(PavFragment pigAvFragment);

    void inject(IndexFragment indexFragment);

    void inject(MeiZiTuFragment meiZiTuFragment);

    void inject(Mm99Fragment mm99Fragment);

    void inject(DownloadingFragment downloadingFragment);

    void inject(FinishedFragment finishedFragment);

    void inject(BaseMainFragment baseMainFragment);

    void inject(MineFragment mineFragment);

    void inject(ForumFragment forumFragment);

    void inject(Forum9IndexFragment forum9IndexFragment);

    void inject(HuaBanFragment huaBanFragment);

    void inject(CommentFragment commentFragment);

    void inject(AuthorFragment authorFragment);

    void inject(AxgleFragment axgleFragment);

    void inject(FavoriteFragment favoriteFragment);
}
